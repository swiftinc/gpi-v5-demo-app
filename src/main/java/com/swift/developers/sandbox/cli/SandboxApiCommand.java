package com.swift.developers.sandbox.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swift.commons.context.Context;
import com.swift.commons.context.KeyStoreContext;
import com.swift.commons.exceptions.NRSignatureException;
import com.swift.commons.exceptions.SignatureContextException;
import com.swift.commons.token.ChannelToken;
import com.swift.commons.token.Token;
import com.swift.developers.sandbox.exception.ApiSessionException;
import com.swift.developers.sandbox.session.SandboxApiSession;
import com.swift.developers.sandbox.util.SwiftSdkUtil;
import com.swift.sdk.common.entity.ConnectionInfo;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.PaymentStatusRequest2;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.model.PaymentScenario7Code;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.model.PaymentStatusRequest3;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.model.BusinessService18Code;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.model.PaymentScenario8Code;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.model.PaymentStatusRequest7;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.BusinessService16Code;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.PaymentStatusRequest5;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.model.PaymentStatusRequest4;
import com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.model.TransactionIndividualStatus5Code;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.CancelTransactionApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.GetChangedPaymentTransactionsApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.GetPaymentTransactionDetailsApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.TransactionCancellationStatusApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threeten.bp.OffsetDateTime;
import picocli.CommandLine.Command;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@Command(
        name = "gpi v5 APIs",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = """
                A simple Java client application consuming gpi v5 APIs using SWIFT SDK.
                """
)
public class SandboxApiCommand implements Callable<Integer> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Value("${swift.connect.config:}")
    private String configFileName;

    @Override
    public Integer call() {
        System.out.println("Using the configuration file - " + configFileName + " to setup the session.");

        SandboxApiSession sess;
        Scanner scan = null;

        try {
            sess = new SandboxApiSession(configFileName, SwiftSdkUtil.CertType.SOFT);
            System.out.println("\nSession is established successfully.");

            displaySessionDetails(sess);

            scan = new Scanner(System.in);
            String userInput;

            do {
                displayApiSelectionMenu();
                userInput = scan.nextLine().trim();

                if (!userInput.isEmpty() && isNumeric(userInput)) {
                    int apiChoice = Integer.parseInt(userInput);
                    callSelectedApi(apiChoice, sess);
                }

            } while (!userInput.equalsIgnoreCase("bye"));
        } catch (ApiSessionException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (scan != null) {
                scan.close();
            }
        }

        // Return an exit code (0 for success, non-zero for errors)
        return 0;
    }

    private void displaySessionDetails(SandboxApiSession sess) {
        System.out.println("Access Token - " + sess.getAccessToken());
        System.out.println("Access Token Expiry - " + sess.getTokenExpiry());
        System.out.println("Refresh Token - " + sess.getRefreshToken());
        System.out.println("Refresh Token Expiry - " + sess.getRefreshExpiry() + "\n");
    }

    private void displayApiSelectionMenu() {
        System.out.print("""
                \n--------------Select the API you would like to call-------------------
                1 - CCT StatusConfirmation
                2 - COV StatusConfirmation
                3 - FIT StatusConfirmation
                4 - INST StatusConfirmation
                5 - Universal StatusConfirmation
                6 - getPaymentTransactionDetails
                7 - getChangedPaymentTransaction
                8 - CancelTransaction
                9 - TransactionCancellationStatus

                Select an API you would like to call or 'bye' to exit:\s
                """);
    }

    private boolean isNumeric(String input) {
        return input.chars().allMatch(Character::isDigit);
    }

    private void callSelectedApi(int apiChoice, SandboxApiSession sess) {
        switch (apiChoice) {
            case 1 -> StatusConfirmationCCT(sess);
            case 2 -> StatusConfirmationCOV(sess);
            case 3 -> StatusConfirmationFIT(sess);
            case 4 -> StatusConfirmationINST(sess);
            case 5 -> StatusConfirmationUni(sess);
            case 6 -> getPaymentTransactionDetails(sess);
            case 7 -> getChangedPaymentTransaction(sess);
            case 8 -> CancelTransaction(sess);
            case 9 -> TransactionCancellationStatus(sess);
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void TransactionCancellationStatus(SandboxApiSession sess) {
        TransactionCancellationStatusApi transactionCancellationStatusApi = new TransactionCancellationStatusApi();
        TransactionCancellationStatusRequest2 reqBody = new TransactionCancellationStatusRequest2();

        String uetr = "97ed4827-7b6f-4491-a06f-b548d5a7512d";

        reqBody.setFrom("BANBUS33XXX");
        reqBody.setServiceLevel(BusinessService11Code.G002);
        reqBody.setCaseIdentification("123");
        reqBody.setInvestigationExecutionStatus(InvestigationExecutionConfirmation5Code.CNCL);
        reqBody.setAssignmentIdentification("resolvedcase123");

        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());
        byte[] signature = CreateNR(basePath, reqBody.toString(), sess);

        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = transactionCancellationStatusApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        transactionCancellationStatusApi.setApiClient(x);
        try {
            transactionCancellationStatusApi.transactionCancellationStatus(reqBody, signature, uetr);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/cancellation/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nHeader Parameters:\n" + "X-SWIFT-Signature: " + new String(signature, StandardCharsets.UTF_8) + "\n" + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void CancelTransaction(SandboxApiSession sess) {
        CancelTransactionApi cancelTransactionApi = new CancelTransactionApi();
        CancelTransactionRequest2 reqBody = new CancelTransactionRequest2();

        String uetr = "422bc102-f141-5b3c-991f-d65b7c27ed85";

        reqBody.setFrom("BANABEBBXXX");
        reqBody.setServiceLevel(BusinessService11Code.G002);
        reqBody.setCaseIdentification("123");
        reqBody.setOriginalInstructionIdentification("XYZ");
        reqBody.setCancellationReasonInformation(CancellationReason8Code.DUPL);
        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());
        byte[] signature = CreateNR(basePath, reqBody.toString(), sess);

        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = cancelTransactionApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        cancelTransactionApi.setApiClient(x);
        try {
            cancelTransactionApi.cancelTransaction(reqBody, signature, uetr);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/cancellation\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nHeader Parameters:\n" + "X-SWIFT-Signature: " + new String(signature, StandardCharsets.UTF_8) + "\n" + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void getChangedPaymentTransaction(SandboxApiSession sess) {
        GetChangedPaymentTransactionsApi getChangedPaymentTransactionsApi = new GetChangedPaymentTransactionsApi();

        OffsetDateTime fromDateTime = OffsetDateTime.parse("2020-04-11T00:00:00.0Z");
        OffsetDateTime toDateTime = OffsetDateTime.parse("2020-04-16T00:00:00.0Z");
        int maxNumber = Integer.parseInt("10");
        String paymentScenario = "CCTR";
        String next = null;


        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = getChangedPaymentTransactionsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        getChangedPaymentTransactionsApi.setApiClient(x);
        try {
            ReadChangedPaymentTransactionsResponse1 response = getChangedPaymentTransactionsApi.getChangedPaymentTransactions(fromDateTime, toDateTime, maxNumber, paymentScenario, next);

            String jsonOutput = GSON.toJson(response);

            String url = "\nURL: " + basePath + "/payments/changed/transactions?from_date_time=" + fromDateTime + "&to_date_time=" + toDateTime + "&maximum_number=" + maxNumber + "\n";

            System.out.println("\nREQUEST" + url + "\nRESPONSE\n " + jsonOutput);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void getPaymentTransactionDetails(SandboxApiSession sess) {
        GetPaymentTransactionDetailsApi getPaymentTransactionDetailsApi = new GetPaymentTransactionDetailsApi();

        String uetr = "97ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = getPaymentTransactionDetailsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        getPaymentTransactionDetailsApi.setApiClient(x);
        try {
            ReadPaymentTransactionDetailsResponse1 response = getPaymentTransactionDetailsApi.getPaymentTransactionDetails(uetr);

            String jsonOutput = GSON.toJson(response);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/transactions" + "\n";

            System.out.println("\nREQUEST" + url + "\nRESPONSE\n " + jsonOutput);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void StatusConfirmationUni(SandboxApiSession sess) {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.api.StatusConfirmationsApi();
        PaymentStatusRequest4 reqBody = new PaymentStatusRequest4();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");

        String uetr = "54ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.status.update.universal.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr, reqBody);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void StatusConfirmationINST(SandboxApiSession sess) {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.api.StatusConfirmationsApi();
        PaymentStatusRequest5 reqBody = new PaymentStatusRequest5();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(BusinessService16Code.G005);
        reqBody.setPaymentScenario(com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.PaymentScenario6Code.CCTR);

        String uetr = "54ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.status.update.inst.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr, reqBody);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void StatusConfirmationFIT(SandboxApiSession sess) {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.api.StatusConfirmationsApi();
        PaymentStatusRequest7 reqBody = new PaymentStatusRequest7();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(BusinessService18Code.G004);
        reqBody.setPaymentScenario(PaymentScenario8Code.FCTR);
        reqBody.setEndToEndIdentification("123Ref");

        String uetr = "97ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.status.update.fit.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr, reqBody);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void StatusConfirmationCOV(SandboxApiSession sess) {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.api.StatusConfirmationsApi();
        PaymentStatusRequest3 reqBody = new PaymentStatusRequest3();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.model.BusinessService12Code.G001);
        reqBody.setPaymentScenario(PaymentScenario7Code.COVE);
        reqBody.setEndToEndIdentification("123Ref");

        String uetr = "54ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.status.update.cov.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr, reqBody);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void StatusConfirmationCCT(SandboxApiSession sess) {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.api.StatusConfirmationsApi();
        PaymentStatusRequest2 reqBody = new PaymentStatusRequest2();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.BusinessService12Code.G001);
        reqBody.setPaymentScenario(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.PaymentScenario6Code.CCTR);

        String uetr = "46ed4827-7b6f-4491-a06f-b548d5a7512d";

        String basePath = com.swift.sdk.gpitracker.v5.status.update.cct.util.GpiTrackerUtil.getBasePath(sess.getConfigJson());

        com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(sess.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr, reqBody);

            String url = "\nURL: " + basePath + "/payments/" + uetr + "/status\n";

            String jsonOutput = GSON.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] CreateNR(String aud, String requestBody, SandboxApiSession sess) {
        Map<String, String> claimsMap = new HashMap<>();
        claimsMap.put("audience", aud);
        String json = new GsonBuilder().disableHtmlEscaping().create().toJson(requestBody);
        claimsMap.put("payload", json);

        ConnectionInfo connection = sess.getConnection();

        try {
            Token token = new ChannelToken();
            Context context = new KeyStoreContext(connection.getCertPath(), connection.getCertPassword(),
                    connection.getCertPassword(), connection.getCertAlias());
            String nrSignature = token.createNRSignature(context, claimsMap);
            return nrSignature.getBytes();
        } catch (SignatureContextException | NRSignatureException e) {
            throw new RuntimeException(e);
        }
    }
}