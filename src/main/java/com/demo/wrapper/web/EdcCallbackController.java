package com.demo.wrapper.web;

import com.demo.wrapper.dao.ProviderSubmodelData;
import com.demo.wrapper.model.EndpointDataReference;
import com.demo.wrapper.model.TransferData;
import com.demo.wrapper.repository.ProviderSubmodelRepository;
import com.demo.wrapper.repository.SentAssetRepository;
import com.demo.wrapper.service.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
public class EdcCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(EdcCallbackController.class);

    private final OkHttpClient client;

    private final CacheService cacheService;

    private final ObjectMapper objectMapper;

    private final SentAssetRepository sentAssetRepository;

    private final ProviderSubmodelRepository providerSubmodelRepository;


    public EdcCallbackController(OkHttpClient client, CacheService cacheService, ObjectMapper objectMapper, SentAssetRepository sentAssetRepository, ProviderSubmodelRepository providerSubmodelRepository) {
        this.client = client;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
        this.sentAssetRepository = sentAssetRepository;
        this.providerSubmodelRepository = providerSubmodelRepository;
    }

    @PostMapping(value = "/endpoint-data-reference")
    public void receiveEdcCallback(@RequestBody EndpointDataReference dataReference) throws JsonProcessingException {
        var contractAgreementId = dataReference.getProperties().get("cid");
        logger.info("EdcCallbackController [receiveEdcCallback] callBackId :: {}\n, contractAgreementId :: {}\n, Endpoint :: {}\n, AuthKey :: {}\n, AuthCode :: {}\n",
                dataReference.getId(), contractAgreementId, dataReference.getEndpoint(), dataReference.getAuthKey(), dataReference.getAuthCode());
        TransferData value = cacheService.getData(contractAgreementId);
        String body = objectMapper.writeValueAsString(value);
//        Request request = new Request.Builder()
//                .url(dataReference.getEndpoint())
//                .addHeader(dataReference.getAuthKey(), dataReference.getAuthCode())
//                .post(okhttp3.RequestBody.create(body, MediaType.get("application/json")))
//                .build();

        Request request = new Request.Builder()
                .url(dataReference.getEndpoint() + "/lookup/shells")
                .addHeader(dataReference.getAuthKey(), dataReference.getAuthCode())
                .post(okhttp3.RequestBody.create(body, MediaType.get("application/json")))
                .build();
        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Transfer Process Has been completed --> {}", response.body().string());
//                Optional<SentAssetDetails> data = sentAssetRepository.findById(value.getReferenceId());
//                if (data.isPresent()) {
//                    data.get().setStatus("SENT");
//                    sentAssetRepository.save(data.get());
//                }
            } else {
                logger.error("Transfer process has been failed.");
            }
        } catch (Exception e) {
            String key = "receiveEcCallback_agreementId_" + contractAgreementId;
            CacheService.socketException.put(key, CacheService.socketException.containsKey(key) ? CacheService.socketException.get(key) + 1 : 1);
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/test")
    public void test(@RequestBody String data) {
        logger.info("Data {} Has Received..", data);
    }

    @GetMapping(value = "/{submodelPath}/submodel")
    public Object getSubmodelDetails(@PathVariable("submodelPath") String submodelPath) throws IOException {
        logger.info("get submodel data from path /{submodelPath}/submodel");
        logger.info("submodel Path:: {aasId}:{submodelId} --> {}", submodelPath);
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos")
                .get()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Request --> {}", response.body().string());
                return response.body().string();
            } else {
                logger.error("process has been failed.");
            }
        }
        return null;
    }

    @GetMapping(value = "/submodel")
    public Object getSubmodelData() throws IOException {
        logger.info("get submodel data from path /submodel");
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos")
                .get()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Request --> {}", response.body().string());
                return response.body().string();
//                Optional<SentAssetDetails> data = sentAssetRepository.findById(value.getReferenceId());
//                if (data.isPresent()) {
//                    data.get().setStatus("SENT");
//                    sentAssetRepository.save(data.get());
//                }
            } else {
                logger.error("process has been failed.");
            }
        }
        return null;
    }

    @GetMapping(value = "/mypath/submodel")
    public Object getSubmodelDataFromPath() throws IOException {
        logger.info("get submodel data from path /mypath/submodel");
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos")
                .get()
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.info("Request --> {}", response.body().string());
                return response.body().string();
//                Optional<SentAssetDetails> data = sentAssetRepository.findById(value.getReferenceId());
//                if (data.isPresent()) {
//                    data.get().setStatus("SENT");
//                    sentAssetRepository.save(data.get());
//                }
            } else {
                logger.error("process has been failed.");
            }
        }
        return null;
    }

    @PostMapping(value = "/provider_backend/data/{assetId}")
    public ProviderSubmodelData saveSubmodelData(@PathVariable("assetId") String assetId, @RequestBody String data) {
        logger.info("<digitalTwinId>-<digitalTwinSubmodelId> ---> {}", assetId);
        ProviderSubmodelData providerSubmodelData = new ProviderSubmodelData();
        providerSubmodelData.setData(data);
        providerSubmodelData.setSubmodelIdentifier(assetId);
        ProviderSubmodelData submodelData = providerSubmodelRepository.save(providerSubmodelData);

        return submodelData;
    }

    @GetMapping(value = "/provider_backend/data/{assetId}")
    public Object getSubmodelData(@PathVariable("assetId") String assetId) throws JsonProcessingException {
        logger.info("call for getSubmodelData for <digitalTwinId>-<digitalTwinSubmodelId> ---> {}", assetId);
        ProviderSubmodelData submodelData = providerSubmodelRepository.findBySubmodelIdentifier(assetId);
        return Objects.nonNull(submodelData.getData())? objectMapper.readValue(submodelData.getData(), Object.class) : null;
    }

}