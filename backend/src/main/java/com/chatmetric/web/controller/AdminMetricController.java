package com.chatmetric.web.controller;

import com.chatmetric.domain.entity.CustMetricDesc;
import com.chatmetric.domain.entity.OrgMetricDesc;
import com.chatmetric.domain.mapper.CustMetricDescMapper;
import com.chatmetric.domain.mapper.OrgMetricDescMapper;
import com.chatmetric.service.MetricVectorSyncService;
import com.chatmetric.service.MetricVectorSyncService.SyncResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics")
public class AdminMetricController {
  private final MetricVectorSyncService metricVectorSyncService;
  private final OrgMetricDescMapper orgMetricDescMapper;
  private final CustMetricDescMapper custMetricDescMapper;

  public AdminMetricController(
      MetricVectorSyncService metricVectorSyncService,
      OrgMetricDescMapper orgMetricDescMapper,
      CustMetricDescMapper custMetricDescMapper) {
    this.metricVectorSyncService = metricVectorSyncService;
    this.orgMetricDescMapper = orgMetricDescMapper;
    this.custMetricDescMapper = custMetricDescMapper;
  }

  @PostMapping("/sync")
  public SyncResult syncAll() {
    return metricVectorSyncService.syncAll();
  }

  @GetMapping("/list")
  public Map<String, Object> list() {
    List<OrgMetricDesc> org = orgMetricDescMapper.findAll();
    List<CustMetricDesc> cust = custMetricDescMapper.findAll();
    Map<String, Object> m = new HashMap<>();
    m.put("org", org);
    m.put("cust", cust);
    return m;
  }
}

