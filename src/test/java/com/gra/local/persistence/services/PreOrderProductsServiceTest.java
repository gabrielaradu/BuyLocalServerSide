package com.gra.local.persistence.services;

import com.gra.local.persistence.EntityHelper;
import com.gra.local.persistence.domain.CurrencyEnum;
import com.gra.local.persistence.domain.QuantityType;
import com.gra.local.persistence.domain.VendorProduct;
import com.gra.local.persistence.services.dtos.ProductAndSelectedQuantity;
import com.gra.local.persistence.services.dtos.VendorProductDto;
import com.twilio.rest.lookups.v1.PhoneNumber;
import com.twilio.rest.lookups.v1.PhoneNumberFetcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("dev")
public class PreOrderProductsServiceTest {

    @InjectMocks
    private PreOrderProductsService subject;

    @Mock
    private TwilioSmsApiWrapper twilioSmsApiWrapper;

    @Mock
    private PhoneNumberFetcher mockedPhoneNumberFetcher;

    private List<VendorProductDto> products;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject.setTwilioSmsApiWrapper(twilioSmsApiWrapper);
        when(twilioSmsApiWrapper.createPhoneNumberFetcher(any())).thenReturn(mockedPhoneNumberFetcher);

        products = new ArrayList<>();
        VendorProduct product = new VendorProduct(1L, "Basil", 1.0D, 12.0D, QuantityType.KG.getIndex(), true, 12D, CurrencyEnum.EURO.getAuthority(), 1L, 5);
        products.add(EntityHelper.convertToAbstractDto(product, VendorProductDto.class));
    }

    @Test
    public void sendOrderDetailsBySmsToVendors_always_sends_a_SmS_to_vendors_with_order_details() {
        PhoneNumber mockedPhoneNumber = mock(PhoneNumber.class);
        String phone = "+1214234325";
        String message = "Testing is not easy but it's rewarding";
        ProductAndSelectedQuantity[] products = new ProductAndSelectedQuantity[] {};

        when(twilioSmsApiWrapper.create(phone, System.getenv("TWILIO_DEV_TEST_PHONE_NR"), message)).thenReturn(Boolean.TRUE);
        when(mockedPhoneNumberFetcher.fetch()).thenReturn(mockedPhoneNumber);
        boolean smsSent = subject.sendOrderDetailsBySmsToVendors(anyString(), products, 1L);

        assertEquals(smsSent, Boolean.TRUE);
    }
}
