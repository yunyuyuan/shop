package cn.phyer.shop.entity;

import java.util.Objects;

public class OrderEntity {
    private Integer id;
    private Integer customerId;
    private Integer merchantId;
    private String crtTm;
    private String addr;
    private Double price;
    private String goods;
    private String state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getCrtTm() {
        return crtTm;
    }

    public void setCrtTm(String crtTm) {
        this.crtTm = crtTm;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(crtTm, that.crtTm) &&
                Objects.equals(addr, that.addr) &&
                Objects.equals(price, that.price) &&
                Objects.equals(goods, that.goods) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, crtTm, addr, price, goods, state);
    }
}
