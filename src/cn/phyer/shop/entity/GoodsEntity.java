package cn.phyer.shop.entity;

import java.util.Objects;

public class GoodsEntity {
    private Integer id;
    private Integer ownerId;
    private String title;
    private String price;
    private String ps;
    private String dcb;
    private Integer favNum;
    private Integer buyNum;
    private Double star;
    private String cover;
    private String cate;
    private String evaluate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getDcb() {
        return dcb;
    }

    public void setDcb(String dcb) {
        this.dcb = dcb;
    }

    public Integer getFavNum() {
        return favNum;
    }

    public void setFavNum(Integer favNum) {
        this.favNum = favNum;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsEntity that = (GoodsEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(price, that.price) &&
                Objects.equals(dcb, that.dcb) &&
                Objects.equals(buyNum, that.buyNum) &&
                Objects.equals(star, that.star) &&
                Objects.equals(cover, that.cover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, title, price, dcb, buyNum, star, cover);
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }
}
