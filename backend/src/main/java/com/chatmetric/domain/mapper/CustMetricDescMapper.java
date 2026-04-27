package com.chatmetric.domain.mapper;

import com.chatmetric.domain.entity.CustMetricDesc;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustMetricDescMapper {
  @Select("SELECT metric_id, metric_name, table_name, column_name, metric_logic_desc FROM cust_metric_desc")
  List<CustMetricDesc> findAll();

  @Select(
      "SELECT metric_id, metric_name, table_name, column_name, metric_logic_desc FROM cust_metric_desc WHERE metric_id = #{metricId}")
  CustMetricDesc findByMetricId(String metricId);
}
