package com.swift.developers.sandbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swift.commons.context.Context;
import com.swift.commons.context.KeyStoreContext;
import com.swift.commons.exceptions.NRSignatureException;
import com.swift.commons.exceptions.SignatureContextException;
import com.swift.commons.exceptions.SignatureGenerationException;
import com.swift.commons.oauth.exceptions.OAuthFailResponseException;
import com.swift.commons.oauth.exceptions.OAuthSessionException;
import com.swift.commons.oauth.exceptions.OAuthValidationException;
import com.swift.commons.oauth.token.OAuthTokenHolder;
import com.swift.commons.token.ChannelToken;
import com.swift.commons.token.Token;
import com.swift.developers.sandbox.exception.ApiSessionException;
import com.swift.developers.sandbox.util.DemoUtil;
import com.swift.sdk.common.entity.ConnectionInfo;
import com.swift.sdk.common.entity.ProxyParameters;
import com.swift.sdk.common.handler.exception.SwiftException;
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
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.GetChangedPaymentTransactionsApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.GetPaymentTransactionDetailsApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.model.*;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.TransactionCancellationStatusApi;
import com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.api.CancelTransactionApi;
import com.swift.sdk.session.impl.SessionImpl;
import com.swift.sdk.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.threeten.bp.OffsetDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DemoApp {

    private static String configFile = null;
    private static JsonObject configJson = null;

    private static ConnectionInfo connInfo = null;

    private static ProxyParameters[] proxyParameters = null;

    private static DemoUtil.CertType certType = DemoUtil.CertType.SOFT;

    private static OAuthTokenHolder tokenHolder = null;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage : DemoApp <Configuration File Name>");
            System.exit(-1);
        } else {
            System.out.println("Using the configuration file - " + args[0] + " to setup the session.");
            configFile = args[0];
        }
        try {
            SessionImpl sess = new SessionImpl();

            if (configFile.endsWith(".yaml")) {
                configJson = DemoUtil.readConfigurationPropertiesYaml(configFile);
            } else if (configFile.endsWith(".json")) {
                configJson = DemoUtil.readConfigurationPropertiesJson(configFile);
            }
            connInfo = Util.createConnectionInfo(configJson);

            if (certType == DemoUtil.CertType.HARD) {
                /* Update fail over logic for SAG slots if needed. */
                /* Uses first slot (slot 0) by default for now. */
                tokenHolder = sess.getAccessToken(connInfo,proxyParameters,0);
            }
            else {
                tokenHolder = sess.getAccessTokenChannelCert(connInfo,proxyParameters);
            }
        //    SandboxApiSession sess = new SandboxApiSession(args[0], Util.CertType.SOFT);
            System.out.println("\nSession is established successfully.");
            System.out.println("Access Token - " + tokenHolder.getAccessToken());
            System.out.println("Access Token Expiry - " + tokenHolder.getTokenExpiry());
            System.out.println("Refresh Token - " + tokenHolder.getRefreshToken());
            System.out.println("Refresh Token Expiry - " + tokenHolder.getRefreshExpiry() + "\n");

            String number = "";
            int num = 0;
            Scanner scan = new Scanner(System.in);

            do {
                scan = new Scanner(System.in);
                System.out.print("\n--------------Select the API you would like to call-------------------\n");
                System.out.print("1 - CCT StatusConfirmation\n" + "2 - COV StatusConfirmation\n" + "3 - FIT StatusConfirmation\n" 
                        + "4 - INST StatusConfirmation\n" + "5 - Universal StatusConfirmation\n" + "6 - getPaymentTransactionDetails\n" 
                        + "7 - getChangedPaymentTransaction\n" + "8 - CancelTransaction\n" + "9 - TransactionCancellationStatus\n" 
                        + "\nSelect an API you would like to call or 'bye' to exit: ");
                number = scan.nextLine();

                if (!number.equalsIgnoreCase("")) {
                    if (StringUtils.isNumeric(number)) {
                        num = Integer.parseInt(number);
                        if (num == 1) {
                            StatusConfirmationCCT();
                        } else if (num == 2) {
                            StatusConfirmationCOV();
                        }else if (num == 3) {
                            StatusConfirmationFIT();
                        }else if (num == 4) {
                            StatusConfirmationINST();
                        }else if (num == 5) {
                            StatusConfirmationUni();
                        }else if (num == 6) {
                            getPaymentTransactionDetails();
                        } else if (num == 7) {
                            getChangedPaymentTransaction();
                        } else if (num == 8) {
                            CancelTransaction();
                        } else if (num == 9) {
                            TransactionCancellationStatus();
                        }
                    }
                }
            } while (number.equalsIgnoreCase("") || !number.equalsIgnoreCase("bye"));

            scan.close();

        } catch (ApiSessionException | SignatureContextException | OAuthValidationException |
                 SignatureGenerationException | OAuthFailResponseException | OAuthSessionException | SwiftException ex) {
            // TODO Auto-generated catch block
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void TransactionCancellationStatus() {
        TransactionCancellationStatusApi transactionCancellationStatusApi = new TransactionCancellationStatusApi();
        TransactionCancellationStatusRequest2 reqBody = new TransactionCancellationStatusRequest2();
        String uetr = "97ed4827-7b6f-4491-a06f-b548d5a7512d";

        reqBody.setFrom("BANBUS33XXX");
        reqBody.setServiceLevel(BusinessService11Code.G002);
        reqBody.setCaseIdentification("123");
        reqBody.setInvestigationExecutionStatus(InvestigationExecutionConfirmation5Code.CNCL);
        reqBody.setAssignmentIdentification("resolvedcase123");

        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(configJson);
        byte[] signature = CreateNR(basePath,reqBody.toString());

        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = transactionCancellationStatusApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        transactionCancellationStatusApi.setApiClient(x);
        try {
            transactionCancellationStatusApi.transactionCancellationStatus(reqBody, signature, uetr);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-transactions-and-cancellations/v5/payments/" + uetr + "/cancellation/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nHeader Parameters:\n" + "X-SWIFT-Signature: " + signature + "\n" + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void CancelTransaction() {
         CancelTransactionApi cancelTransactionApi = new CancelTransactionApi();
         CancelTransactionRequest2 reqBody = new CancelTransactionRequest2();
         String uetr = "422bc102-f141-5b3c-991f-d65b7c27ed85";

         reqBody.setFrom("BANABEBBXXX");
         reqBody.setServiceLevel(BusinessService11Code.G002);
         reqBody.setCaseIdentification("123");
         reqBody.setOriginalInstructionIdentification("XYZ");
         reqBody.setCancellationReasonInformation(CancellationReason8Code.DUPL);
         String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(configJson);
         byte[] signature = CreateNR(basePath,reqBody.toString());


         com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = cancelTransactionApi.getApiClient();
         x.setBasePath(basePath);
         x.setAccessToken(tokenHolder.getAccessToken());
         cancelTransactionApi.setApiClient(x);
        try {
            cancelTransactionApi.cancelTransaction(reqBody, signature, uetr);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-transactions-and-cancellations/v5/payments/" + uetr + "/cancellation\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nHeader Parameters:\n" + "X-SWIFT-Signature: " + signature + "\n" + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }

    }

    private static void getChangedPaymentTransaction() {
        GetChangedPaymentTransactionsApi getChangedPaymentTransactionsApi = new GetChangedPaymentTransactionsApi();

        OffsetDateTime fromDateTime = OffsetDateTime.parse("2020-04-11T00:00:00.0Z");
        OffsetDateTime toDateTime = OffsetDateTime.parse("2020-04-16T00:00:00.0Z");
        int maxNumber = Integer.parseInt("10");
        String paymentScenario = "CCTR";
        String next = null;


        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(configJson);


        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = getChangedPaymentTransactionsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        getChangedPaymentTransactionsApi.setApiClient(x);
        try {
            ReadChangedPaymentTransactionsResponse1 response = getChangedPaymentTransactionsApi.getChangedPaymentTransactions(fromDateTime, toDateTime, maxNumber, paymentScenario, next);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(response);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-transactions-and-cancellations/v5/payments/changed/transactions?from_date_time=" + fromDateTime + "&to_date_time=" + toDateTime + "&maximum_number=" + maxNumber + "\n";

            System.out.println("\nREQUEST" + url + "\nRESPONSE\n " + jsonOutput);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getPaymentTransactionDetails() {
        GetPaymentTransactionDetailsApi getPaymentTransactionDetailsApi = new GetPaymentTransactionDetailsApi();

        String uetr = "97ed4827-7b6f-4491-a06f-b548d5a7512d";


        String basePath = com.swift.sdk.gpitracker.v5.transactionsandcancellations.util.GpiTrackerUtil.getBasePath(configJson);


        com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiClient x = getPaymentTransactionDetailsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        getPaymentTransactionDetailsApi.setApiClient(x);
        try {
            ReadPaymentTransactionDetailsResponse1 response = getPaymentTransactionDetailsApi.getPaymentTransactionDetails(uetr);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(response);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-transactions-and-cancellations/v5/payments/" + uetr + "/transactions" + "\n";

            System.out.println("\nREQUEST" + url + "\nRESPONSE\n " + jsonOutput);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.transactionsandcancellations.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void StatusConfirmationUni() {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.api.StatusConfirmationsApi();
        PaymentStatusRequest4 reqBody = new PaymentStatusRequest4();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");

        String uetr = "54ed4827-7b6f-4491-a06f-b548d5a7512d";


        String basePath = com.swift.sdk.gpitracker.v5.status.update.universal.util.GpiTrackerUtil.getBasePath(configJson);

        com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr,reqBody);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-uc-cct/v5/payments/" + uetr + "/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.universal.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void StatusConfirmationINST() {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.api.StatusConfirmationsApi();
        PaymentStatusRequest5 reqBody = new PaymentStatusRequest5();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(BusinessService16Code.G005);
        reqBody.setPaymentScenario(com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.model.PaymentScenario6Code.CCTR);

        String uetr = "54ed4827-7b6f-4491-a06f-b548d5a7512d";


        String basePath = com.swift.sdk.gpitracker.v5.status.update.inst.util.GpiTrackerUtil.getBasePath(configJson);

        com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr,reqBody);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-gcct-inst/v5/payments/" + uetr + "/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.inst.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void StatusConfirmationFIT() {
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


        String basePath = com.swift.sdk.gpitracker.v5.status.update.fit.util.GpiTrackerUtil.getBasePath(configJson);

        com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr,reqBody);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-gfit/v5/payments/" + uetr + "/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.fit.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void StatusConfirmationCOV() {
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


        String basePath = com.swift.sdk.gpitracker.v5.status.update.cov.util.GpiTrackerUtil.getBasePath(configJson);

        com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr,reqBody);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-gcov/v5/payments/" + uetr + "/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.cov.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void StatusConfirmationCCT() {
        com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.api.StatusConfirmationsApi statusConfirmationsApi = new com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.api.StatusConfirmationsApi();
        PaymentStatusRequest2 reqBody = new PaymentStatusRequest2();
        reqBody.setFrom("BANCUS33XXX");
        reqBody.setInstructionIdentification("jkl000");
        reqBody.setTransactionStatus(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.TransactionIndividualStatus5Code.ACCC);
        reqBody.setTrackerInformingParty("BANABEBBXXX");
        reqBody.setServiceLevel(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.BusinessService12Code.G001);
        reqBody.setPaymentScenario(com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.model.PaymentScenario6Code.CCTR);

        String uetr = "46ed4827-7b6f-4491-a06f-b548d5a7512d";


        String basePath = com.swift.sdk.gpitracker.v5.status.update.cct.util.GpiTrackerUtil.getBasePath(configJson);

        com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.ApiClient x = statusConfirmationsApi.getApiClient();
        x.setBasePath(basePath);
        x.setAccessToken(tokenHolder.getAccessToken());
        statusConfirmationsApi.setApiClient(x);
        try {
            statusConfirmationsApi.statusConfirmations(uetr,reqBody);

            String url = "\nURL: https://sandbox.swift.com/swift-apitracker-gcct/v5/payments/" + uetr + "/status\n";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(reqBody);
            String response = "\n200 OK";

            System.out.println("\nREQUEST" + url + "\nBody:\n" + jsonOutput + "\n" + "\nRESPONSE" + response);
        } catch (com.swift.sdk.oas.gpi.tracker.v5.status.update.cct.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] CreateNR(String aud, String requestBody){

        Map<String, String> claimsMap = new HashMap<>();
        claimsMap.put("audience", aud);
        String json = new GsonBuilder().disableHtmlEscaping().create().toJson(requestBody);
        claimsMap.put("payload", json);

        try {
            Token token = new ChannelToken();
            Context context = new KeyStoreContext(connInfo.getCertPath(), connInfo.getCertPassword(),
                    connInfo.getCertPassword(), connInfo.getCertAlias());
            String nrSignature = token.createNRSignature(context, claimsMap);
            return nrSignature.getBytes();
        } catch (SignatureContextException | NRSignatureException e) {
            throw new RuntimeException(e);
        }

    }
}