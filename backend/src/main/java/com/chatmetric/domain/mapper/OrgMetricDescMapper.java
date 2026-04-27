package com.chatmetric.domain.mapper;

import com.chatmetric.domain.entity.OrgMetricDesc;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrgMetricDescMapper {
  @Select("SELECT metric_id, metric_name, table_name, metric_logic_desc FROM org_metric_desc")
  List<OrgMetricDesc> findAll();

  @Select(
      "SELECT metric_id, metric_name, table_name, metric_logic_desc FROM org_metric_desc WHERE metric_id = #{metricId}")
  OrgMetricDesc findByMetricId(String metricId);
}
