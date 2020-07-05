package cn.phyer.shop.entity;

import java.util.Objects;

public class MerchantEntity {
    private Integer id;
    private String name;
    private String email;
    private String pwd;
    private Double star;
    private Integer starNum;
    private Integer favNum;
    private Integer sellNum;
    private String cover;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public Integer getStarNum() {
        return starNum;
    }

    public void setStarNum(Integer starNum) {
        this.starNum = starNum;
    }

    public Integer getFavNum() {
        return favNum;
    }

    public void setFavNum(Integer favNum) {
        this.favNum = favNum;
    }

    public Integer getSellNum() {
        return sellNum;
    }

    public void setSellNum(Integer sellNum) {
        this.sellNum = sellNum;
    }
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerchantEntity that = (MerchantEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(star, that.star) &&
                Objects.equals(favNum, that.favNum) &&
                Objects.equals(cover, that.cover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, star, favNum, cover);
    }
}
