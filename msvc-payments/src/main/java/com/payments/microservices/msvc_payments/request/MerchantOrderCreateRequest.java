package com.payments.microservices.msvc_payments.request;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantOrderCreateRequest {

private String externalReference;

private String notificationUrl;

private List <Item> items;

private String customerInfo;

private String description; 

private Date expirationDate;

private Map <String, String> metaData;



}
