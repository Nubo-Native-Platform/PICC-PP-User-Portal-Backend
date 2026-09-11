package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.dto.PodUsageDto;
import com.nnp.dashboard.service.KafkaMessageService;
import com.nnp.dashboard.service.NubonsPortalService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PodUsageDataSchedulerTest {

    @Test
    void publishesOneListPerNamespaceChunkAndRetriesOnlyTheFailedChunk() {
        KafkaMessageService kafka = mock(KafkaMessageService.class);
        NubonsPortalService portal = mock(NubonsPortalService.class);
        PodUsageCollectionProperties properties = new PodUsageCollectionProperties();
        properties.setNamespaces(List.of("alpha", "beta", "gamma"));
        properties.setNamespaceBatchSize(2);
        properties.setCollectionWindowMs(1);
        properties.setDelayBetweenChunksMs(0);

        List<String> firstChunk = List.of("alpha", "beta");
        List<String> secondChunk = List.of("gamma");
        when(portal.getPodUsageStatsForNamespaces(firstChunk))
                .thenReturn(List.of(new PodUsageDto("alpha", "a", 1D, 2D, 3D)));
        doThrow(new RuntimeException("temporary timeout"))
                .doThrow(new RuntimeException("temporary timeout"))
                .when(portal).getPodUsageStatsForNamespaces(secondChunk);

        new PodUsageDataScheduler(kafka, portal, properties, "pod-usage-topic")
                .fetchAndSendPodUsageData();

        ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
        verify(kafka, times(1)).sendMessage(eq("pod-usage-topic"), key.capture(), anyList());
        assertEquals(true, key.getValue().matches("pod-usage:.+?:1/2"));
        verify(portal, times(1)).getPodUsageStatsForNamespaces(firstChunk);
        verify(portal, times(2)).getPodUsageStatsForNamespaces(secondChunk);
    }
}
