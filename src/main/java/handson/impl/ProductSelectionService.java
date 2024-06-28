package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.Versioned;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.product.ProductResourceIdentifierBuilder;
import com.commercetools.api.models.product_selection.*;
import com.commercetools.api.models.store.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class ProductSelectionService {

    final ProjectApiRoot apiRoot;

    public ProductSelectionService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }


    /**
     * Gets a product selection by key.
     *
     * @return the product selection completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> getProductSelectionByKey(final String productSelectionKey) {
        return
                apiRoot
                        .productSelections()
                        .withKey(productSelectionKey)
                        .get()
                        .execute();
    }

    /**
     * Gets a store by key.
     *
     * @return the store completion stage
     */
    public CompletableFuture<ApiHttpResponse<Store>> getStoreByKey(final String storeKey) {
        return
                apiRoot
                        .stores()
                        .withKey(storeKey)
                        .get()
                        .execute();
    }

    /**
     * Creates a new product selection.
     *
     * @return the product selection creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> createProductSelection(final String productSelectionKey, final String name) {
        return
                apiRoot.productSelections().post(
                        ProductSelectionDraftBuilder.of()
                                .key(productSelectionKey)
                                .name(LocalizedStringBuilder.of().addValue("de-De", name).build())
                                .build()
                ).execute();
    }


    public CompletableFuture<ApiHttpResponse<ProductSelection>> addProductToProductSelection(
            final ApiHttpResponse<ProductSelection> productSelectionApiHttpResponse,
            final String productKey) {


        return apiRoot.productSelections().update(
                Versioned.of(productSelectionApiHttpResponse.getBody()),
                Collections.singletonList(
                        ProductSelectionUpdateAction.addProductBuilder()
                                .product(
                                        ProductResourceIdentifierBuilder.of()
                                                .key(productKey)
                                                .build())
                                .build())
        ).execute();
    }

    public CompletableFuture<ApiHttpResponse<Store>> addProductSelectionToStore(
            final ApiHttpResponse<Store> storeApiHttpResponse,
            final ApiHttpResponse<ProductSelection> productSelectionApiHttpResponse) {

        return apiRoot.stores().update(Versioned.of(storeApiHttpResponse.getBody()),
                Collections.singletonList(
                        StoreUpdateActionBuilder.of()
                                .addProductSelectionBuilder()
                                .productSelection(productSelectionApiHttpResponse.getBody().get().toResourceIdentifier())
                                .active(true)
                                .build()
                )).execute();
    }

    public CompletableFuture<ApiHttpResponse<ProductSelectionProductPagedQueryResponse>> getProductsInProductSelection(
            final String productSelectionKey) {
        return apiRoot.productSelections()
                .withKey(productSelectionKey)
                .products()
                .get()
                .addExpand(() -> "product")
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ProductsInStorePagedQueryResponse>> getProductsInStore(
            final String storeKey) {

        return apiRoot.with()
                .inStoreKeyWithStoreKeyValue(storeKey)
                .productSelectionAssignments()
                .get()
                .addExpand(() -> "product")
                .execute();
    }
}
