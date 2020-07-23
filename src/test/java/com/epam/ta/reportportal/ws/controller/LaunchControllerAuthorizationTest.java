package com.epam.ta.reportportal.ws.controller;

import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.commons.querygen.FilterCondition;
import com.epam.ta.reportportal.dao.LaunchRepository;
import com.epam.ta.reportportal.entity.enums.LaunchModeEnum;
import com.epam.ta.reportportal.entity.enums.StatusEnum;
import com.epam.ta.reportportal.entity.launch.Launch;
import com.epam.ta.reportportal.security.authorization.AccessEntryBuilder;
import com.epam.ta.reportportal.security.authorization.IllegalUserAccessEntry;
import com.epam.ta.reportportal.ws.BaseMvcTest;
import com.epam.ta.reportportal.ws.model.BulkInfoUpdateRQ;
import com.epam.ta.reportportal.ws.model.BulkRQ;
import com.epam.ta.reportportal.ws.model.DeleteBulkRQ;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributeResource;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.epam.ta.reportportal.ws.model.attribute.UpdateItemAttributeRQ;
import com.epam.ta.reportportal.ws.model.launch.MergeLaunchesRQ;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.epam.ta.reportportal.ws.model.launch.UpdateLaunchRQ;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.ta.reportportal.commons.querygen.constant.GeneralCriteriaConstant.CRITERIA_PROJECT_ID;
import static com.epam.ta.reportportal.ws.model.launch.Mode.DEBUG;
import static com.epam.ta.reportportal.ws.model.launch.Mode.DEFAULT;
import static java.util.stream.Collectors.toMap;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/db/launch/launch-fill.sql", "/db/security/authorization_verification.sql"})
public class LaunchControllerAuthorizationTest extends BaseMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LaunchRepository launchRepository;

    private StartLaunchRQ validLaunchSample() {
        String name = "some launch name";
        StartLaunchRQ startLaunchRQ = new StartLaunchRQ();
        startLaunchRQ.setDescription("some description");
        startLaunchRQ.setName(name);
        startLaunchRQ.setStartTime(new Date());
        startLaunchRQ.setMode(DEFAULT);
        startLaunchRQ.setAttributes(Sets.newHashSet(new ItemAttributesRQ("key", "value")));
        return startLaunchRQ;
    }

    private UpdateLaunchRQ validUpdateLaunchRQ() {
        UpdateLaunchRQ rq = new UpdateLaunchRQ();
        rq.setMode(DEFAULT);
        rq.setDescription("description");
        rq.setAttributes(Sets.newHashSet(new ItemAttributeResource("test", "test")));
        return rq;
    }

    @Test
    void createLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post(DEFAULT_PROJECT_BASE_URL + "/launch/")
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(validLaunchSample()))
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void updateLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/3/update")
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(validUpdateLaunchRQ()))
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/2")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getLaunchStringAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/4850a659-ac26-4a65-8ea4-a6756a57fb92")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getLaunchUuidAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/uuid/4850a659-ac26-4a65-8ea4-a6756a57fb92")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getDebugLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(SUPERADMIN_PROJECT_BASE_URL + "/launch/mode")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void compareLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/compare?ids=1,2")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private MergeLaunchesRQ getMergeLaunchesRQSample() {
        MergeLaunchesRQ rq = new MergeLaunchesRQ();
        HashSet<Long> set = new HashSet<>();
        set.add(1L);
        set.add(2L);
        rq.setLaunches(set);
        rq.setName("Merged");
        rq.setMergeStrategyType("BASIC");
        rq.setStartTime(new Date());
        rq.setEndTime(new Date());
        return rq;
    }

    @Test
    void mergeLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(post(DEFAULT_PROJECT_BASE_URL + "/launch/merge")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(getMergeLaunchesRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void deleteLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete(DEFAULT_PROJECT_BASE_URL + "/launch/1")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getStatusAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/status?ids=1")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private FinishExecutionRQ getFinishExecutionRQSample() {
        final FinishExecutionRQ finishExecutionRQ = new FinishExecutionRQ();
        finishExecutionRQ.setEndTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()));
        finishExecutionRQ.setStatus(StatusEnum.PASSED.name());
        return finishExecutionRQ;
    }

    @Test
    void finishLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/befef834-b2ef-4acf-aea3-b5a5b15fd93c/finish")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(getFinishExecutionRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void forceFinishLaunchAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/3/stop")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(getFinishExecutionRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private BulkRQ<Long, FinishExecutionRQ> getbulkRQSample() {
        final BulkRQ<Long, FinishExecutionRQ> bulkRQ = new BulkRQ<>();
        bulkRQ.setEntities(Stream.of(3L, 5L).collect(toMap(it -> it, it -> {
            FinishExecutionRQ finishExecutionRQ = new FinishExecutionRQ();
            finishExecutionRQ.setStatus(StatusEnum.PASSED.name());
            finishExecutionRQ.setEndTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()));
            return finishExecutionRQ;
        })));
        return bulkRQ;
    }

    @Test
    void bulkForceFinishAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/stop")
                    .with(token(entry.getAccessToken()))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getbulkRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllOwnersAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/owners?filter.cnt.user=def")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAllLaunchNamesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/names?filter.cnt.name=test")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private DeleteBulkRQ getDeleteBulkRQSample() {
        DeleteBulkRQ deleteBulkRQ = new DeleteBulkRQ();
        List<Long> ids = Lists.newArrayList(1L, 2L);
        deleteBulkRQ.setIds(ids);
        return deleteBulkRQ;
    }

    @Test
    void bulkDeleteLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(delete(DEFAULT_PROJECT_BASE_URL + "/launch")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(getDeleteBulkRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private BulkRQ<Long, UpdateLaunchRQ> getBulkRQwithEntitiesSample() {
        final List<Long> ids = launchRepository.findByFilter(Filter.builder()
                .withTarget(Launch.class)
                .withCondition(FilterCondition.builder().eq(CRITERIA_PROJECT_ID, String.valueOf(2L)).build())
                .build()).stream().filter(it -> it.getMode() == LaunchModeEnum.DEFAULT).map(Launch::getId).collect(Collectors.toList());
        final Map<Long, UpdateLaunchRQ> entities = ids.stream().collect(toMap(it -> it, it -> {
            final UpdateLaunchRQ updateLaunchRQ = new UpdateLaunchRQ();
            updateLaunchRQ.setMode(DEBUG);
            return updateLaunchRQ;
        }));
        final BulkRQ<Long, UpdateLaunchRQ> bulkRQ = new BulkRQ<>();
        bulkRQ.setEntities(entities);
        return bulkRQ;
    }

    @Test
    void bulkMoveToDebugAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/update")
                    .with(token(entry.getAccessToken()))
                    .content(objectMapper.writeValueAsBytes(getBulkRQwithEntitiesSample()))
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL
                    + "/launch?page.page=1&page.size=50&page.sort=statistics$defects$product_bug$total,ASC")
                    .contentType(APPLICATION_JSON)
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getLatestLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL
                    + "/launch/latest?page.page=1&page.size=10&page.sort=name,ASC")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAttributeKeysAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL
                    + "/launch/attribute/keys?filter.cnt.attributeKey=browser")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getAttributeValuesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(
                    DEFAULT_PROJECT_BASE_URL + "/launch/attribute/values?filter.eq.attributeKey=browser&filter.cnt.attributeValue=ch")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void getProjectLaunchesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    @Test
    void exportAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(get(DEFAULT_PROJECT_BASE_URL + "/launch/1/report")
                    .with(token(entry.getAccessToken())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private BulkInfoUpdateRQ getCreateBulkInfoUpdateRQSample() {
        BulkInfoUpdateRQ request = new BulkInfoUpdateRQ();
        List<Long> launchIds = Arrays.asList(1L, 2L, 3L, 4L);
        request.setIds(launchIds);
        BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
        description.setAction(BulkInfoUpdateRQ.Action.CREATE);
        String comment = "created";
        description.setComment(comment);
        request.setDescription(description);
        UpdateItemAttributeRQ updateItemAttributeRQ = new UpdateItemAttributeRQ();
        updateItemAttributeRQ.setAction(BulkInfoUpdateRQ.Action.UPDATE);
        updateItemAttributeRQ.setFrom(new ItemAttributeResource("testKey", "testValue"));
        updateItemAttributeRQ.setTo(new ItemAttributeResource("updatedKey", "updatedValue"));
        request.setAttributes(Lists.newArrayList(updateItemAttributeRQ));
        return request;
    }

    @Test
    void bulkUpdateItemAttributesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/info")
                    .with(token(entry.getAccessToken()))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getCreateBulkInfoUpdateRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private BulkInfoUpdateRQ getUpdateBulkInfoUpdateRQ() {
        BulkInfoUpdateRQ request = new BulkInfoUpdateRQ();
        List<Long> launchIds = Arrays.asList(1L, 2L, 3L, 4L);
        request.setIds(launchIds);
        BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
        description.setAction(BulkInfoUpdateRQ.Action.UPDATE);
        String comment = "updated";
        description.setComment(comment);
        request.setDescription(description);
        UpdateItemAttributeRQ updateItemAttributeRQ = new UpdateItemAttributeRQ();
        updateItemAttributeRQ.setAction(BulkInfoUpdateRQ.Action.CREATE);
        updateItemAttributeRQ.setTo(new ItemAttributeResource("createdKey", "createdValue"));
        request.setAttributes(Lists.newArrayList(updateItemAttributeRQ));
        return request;
    }

    @Test
    void bulkCreateAttributesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/info")
                    .with(token(entry.getAccessToken()))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getUpdateBulkInfoUpdateRQ())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

    private BulkInfoUpdateRQ getDeleteBulkInfoUpdateRQSample() {
        BulkInfoUpdateRQ request = new BulkInfoUpdateRQ();
        List<Long> launchIds = Arrays.asList(1L, 2L, 3L, 4L);
        request.setIds(launchIds);
        BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
        description.setAction(BulkInfoUpdateRQ.Action.CREATE);
        String comment = "created";
        description.setComment(comment);
        request.setDescription(description);
        UpdateItemAttributeRQ updateItemAttributeRQ = new UpdateItemAttributeRQ();
        updateItemAttributeRQ.setAction(BulkInfoUpdateRQ.Action.DELETE);
        updateItemAttributeRQ.setFrom(new ItemAttributeResource("testKey", "testValue"));
        request.setAttributes(Lists.newArrayList(updateItemAttributeRQ));
        return request;
    }

    @Test
    void bulkDeleteAttributesAuthorization() throws Exception {
        for (IllegalUserAccessEntry entry : AccessEntryBuilder.createAccessEntries(oAuthHelper)) {
            mockMvc.perform(put(DEFAULT_PROJECT_BASE_URL + "/launch/info")
                    .with(token(entry.getAccessToken()))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(getDeleteBulkInfoUpdateRQSample())))
                    .andExpect(status().is(entry.getAccessStatus()));
        }
    }

}
