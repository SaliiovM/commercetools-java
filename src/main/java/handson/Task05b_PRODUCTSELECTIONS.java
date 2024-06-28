package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product_selection.AssignedProductReference;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionAssignment;
import com.commercetools.api.models.store.Store;
import handson.impl.ApiPrefixHelper;
import handson.impl.ProductSelectionService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;

public class Task05b_PRODUCTSELECTIONS {
    private final static String STORE_KEY = "best-store-ever";
    private static final String PRODUCT_SELECTION_KEY = "mh-berlin-product-selection";
    private static final String PRODUCT_KEY = "best-berlin-t-short";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(Task05b_PRODUCTSELECTIONS.class.getName());
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(globalApiClientPrefix);

        final ProductSelectionService productSelectionService = new ProductSelectionService(client);

        ProductSelection productSelection = productSelectionService
                .createProductSelection(PRODUCT_SELECTION_KEY, "Best Berlin Product Selection (DE-DE)")
                .get()
                .getBody();

        logger.info("Created product selection: " + productSelection.getId());

        CompletableFuture<ApiHttpResponse<ProductSelection>> productToProductSelection = productSelectionService.addProductToProductSelection(
                productSelectionService.getProductSelectionByKey(PRODUCT_SELECTION_KEY).get(), PRODUCT_KEY);

        logger.info("Added product to product selection: " + productToProductSelection.get().getBody().getId());

        productSelectionService.getStoreByKey(STORE_KEY).thenAccept(store -> {
            try {
                CompletableFuture<ApiHttpResponse<Store>> assigned = productSelectionService
                        .addProductSelectionToStore(store, productSelectionService.getProductSelectionByKey(PRODUCT_SELECTION_KEY).get());
                logger.info("Product selections assigned to the store: " + assigned.get().getBody().getKey());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error assigning product selection to the store", e);
            }
        }).get();

        List<ProductSelectionAssignment> results = productSelectionService.getProductsInStore(STORE_KEY).get().getBody().getResults();

        logger.info("Products in store: ");
        results.forEach(productSelectionAssignment -> logger.info(productSelectionAssignment.getProduct().getObj().getKey()));

        List<AssignedProductReference> assignedProductReferences
                = productSelectionService.getProductsInProductSelection(PRODUCT_SELECTION_KEY)
                .get()
                .getBody()
                .getResults();

        logger.info("Products in product selection: ");
        assignedProductReferences.forEach(assignedProductReference -> logger.info(assignedProductReference.getProduct().getObj().getKey()));

    }
}
