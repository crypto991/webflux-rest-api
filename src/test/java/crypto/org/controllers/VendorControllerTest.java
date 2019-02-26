package crypto.org.controllers;

import crypto.org.domain.Vendor;
import crypto.org.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {

    VendorRepository vendorRepository;

    WebTestClient webTestClient;

    VendorController vendorController;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("djoka").lastName("rokvic").build(),
                        Vendor.builder().firstName("losmi").lastName("cuic").build()));

        webTestClient.get().uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);

    }

    @Test
    public void getById() {
        BDDMockito.given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().firstName("djoka").lastName("rokvic").build()));

        webTestClient.get().uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Vendor.class);


    }

    @Test
    public void testCreateCategory() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> catToSaveMono = Mono.just(Vendor.builder().firstName("duckaj").lastName("kurac").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(catToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateCategory(){
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> catToSaveMono = Mono.just(Vendor.builder().firstName("duckaj").lastName("kurac").build());

        webTestClient.put()
                .uri("/api/v1/vendors/asdadas")
                .body(catToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void testPatchWithChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> catToUpdateMono = Mono.just(Vendor.builder().firstName("New Description").lastName("pusiga").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/asdfasdf")
                .body(catToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchNoChanges() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> catToUpdateMono = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/asdfasdf")
                .body(catToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}