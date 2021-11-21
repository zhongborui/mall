package com.arui.mall.model.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀预订单
 */

@Data
public class PrepareSeckillOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;

	private SeckillProduct seckillProduct;

	private Integer buyNum;

	private String prepareOrderCode;
}
