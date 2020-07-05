package cn.phyer.shop.entity;

import java.util.Objects;

public class CustomerEntity {
    private Integer id;
    private String name;
    private String email;
    private String pwd;
    private String cover;
    private String cart;
    private String favGoods;
    private String favStore;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCart() {
        return cart;
    }

    public void setCart(String cart) {
        this.cart = cart;
    }

    public String getFavGoods() {
        return favGoods;
    }

    public void setFavGoods(String favGoods) {
        this.favGoods = favGoods;
    }

    public String getFavStore() {
        return favStore;
    }

    public void setFavStore(String favStore) {
        this.favStore = favStore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerEntity that = (CustomerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(pwd, that.pwd) &&
                Objects.equals(cover, that.cover) &&
                Objects.equals(cart, that.cart) &&
                Objects.equals(favStore, that.favStore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, pwd, cover, cart, favStore);
    }
}
