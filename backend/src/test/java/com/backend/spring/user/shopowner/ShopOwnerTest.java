package com.backend.spring.user.shopowner;

import com.backend.spring.address.Address;
import com.backend.spring.shop.Shop;
import com.backend.spring.shop.ShopRepository;
import com.backend.spring.user.role.RoleEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

@SpringBootTest
class ShopOwnerTest {
    @Autowired
    private ShopOwnerRepository shopOwnerRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopOwnerSaveHelper saveHelper;

    private ShopOwner shopOwner;

    private Shop shop;
    private Address address;

    @AfterEach
    void tearDown() {
        shopRepository.deleteAll();
        shopOwnerRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        address = new Address("StreetNum", "Street", "PostalCode", "City", "Prov");

        shop = new Shop("Sayyara Shop", address, "416-412-3123", "sayyara@gmail.com");

        shopOwner = new ShopOwner("abc", "Bob", "bob@gmail.com", "416-123-1234", "bob", "password");
        shopOwner = saveHelper.saveAndFlush(shopOwner, shop, address);

    }

    @Test
    void checkShopOwnerSaved() {
        assertThat(saveHelper.save(shopOwner, shop, address).getUsername()).isEqualTo(shopOwner.getUsername());
    }

    @Test
    void checkShopsUnique() {
        ShopOwner shopOwner1 = new ShopOwner("abc", "Bob", "bob@gmail.com", "416-123-1234", "bob", "password");
        try {
            shopOwner = saveHelper.saveAndFlush(shopOwner, shop, address);
            shopOwner1 = saveHelper.saveAndFlush(shopOwner1, shop, address);
            fail("Shops are the same and app doesn't crash: \n\t" + shopOwner + "\n\t" + shopOwner1);
        } catch (DataIntegrityViolationException ignored) {
            assertThat(shopOwner1).isNotNull();
            assertThat(shopOwner).isNotNull();
        }
    }

    @Test
    void checkShopOwnerExists() {
        assertThat(shopOwner.getFirstName()).isEqualTo("abc");
    }

    @Test
    void checkRoleExists() {
        assertThat(shopOwner.getRoles().stream().findFirst().orElseThrow().getName()).isEqualTo(RoleEnum.SHOP_OWNER);
    }

    @Test
    void checkShopExists() {
        assertThat(shopOwner.getShop().getName()).isEqualTo("Sayyara Shop");
    }

    @Test
    void checkAddressExists() {
        assertThat(shopOwner.getShop().getAddress().getStreet()).isNotNull();
    }
}