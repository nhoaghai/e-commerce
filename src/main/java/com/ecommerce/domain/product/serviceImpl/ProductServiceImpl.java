package com.ecommerce.domain.product.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.member.repository.SellerRepository;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.dto.response.ShopProductResponse;
import com.ecommerce.domain.product.exception.ProductException;
import com.ecommerce.domain.product.model.Category;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final SellerRepository sellerRepository;

    @Override
    public PageResponseDto<ProductResponse> findAllProduct(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);

        List<ProductResponse> data = page.getContent().stream()
                .map(product -> {
                    ProductResponse response = modelMapper.map(product, ProductResponse.class);
                    response.setCategoryName(product.getCategory().getCategoryName());
                    response.setShopName(product.getSeller().getShopName());
                    return response;
                })
                .toList();

        PageResponseDto<ProductResponse> pageResponseDto = new PageResponseDto<>();
        pageResponseDto.setData(data);
        pageResponseDto.setTotalPage(page.getTotalPages());
        pageResponseDto.setSize(page.getSize());
        pageResponseDto.setPageNumber(page.getNumber());
        pageResponseDto.setSort(page.getSort().toString());

        return pageResponseDto;
    }

    @Override
    public List<ProductResponse> findAllByProductNameOrDes(String keyword) {
        List<Product> products = productRepository.findAllByProductNameContainingOrDescriptionContaining(keyword, keyword);
        if (products.isEmpty()){
            throw ProductException.notFound("No product found matching the search criteria");
        }else {
            return products.stream()
                    .map(product -> {
                        ProductResponse response = modelMapper.map(product, ProductResponse.class);
                        response.setCategoryName(product.getCategory().getCategoryName());
                        response.setShopName(product.getSeller().getShopName());
                        return response;
                    })
                    .toList();
        }
    }

    @Override
    public List<ProductResponse> findAllByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findAllByCategoryCategoryId(categoryId);
        if (products.isEmpty()){
            throw ProductException.notFound("No product found matching the categoryId");
        }else {
            return products.stream()
                    .map(product -> {
                        ProductResponse response = modelMapper.map(product, ProductResponse.class);
                        response.setCategoryName(product.getCategory().getCategoryName());
                        response.setShopName(product.getSeller().getShopName());
                        return response;
                    })
                    .toList();
        }
    }

    @Override
    public ProductResponse findByProductId(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product == null){
            throw new ProductException("Not found product with sku");
        }else {
            ProductResponse response = modelMapper.map(product, ProductResponse.class);
            response.setCategoryName(product.getCategory().getCategoryName());
            response.setShopName(product.getSeller().getShopName());
            return response;
        }
    }

    @Override
    public ProductResponse findByProductSku(String sku) {
        Product product = productRepository.findByProductSku(sku);
        if (product == null){
            throw new ProductException("Not found product with id");
        } else {
            ProductResponse response = modelMapper.map(product, ProductResponse.class);
            response.setCategoryName(product.getCategory().getCategoryName());
            response.setShopName(product.getSeller().getShopName());
            return response;
        }
    }

    @Override
    public List<ShopProductResponse> findAllShopProduct(String shopName) {
        Seller seller = sellerRepository.findByShopName(shopName);
        if(seller == null) {
            throw new ProductException("No shop of this name found");
        }
        List<Product> products = productRepository.findAllBySeller(sellerRepository.findByShopName(shopName));
        Set<Category> categories = products.stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
        List<ShopProductResponse> shopProductResponses = new ArrayList<>();

        HashMap<String, ArrayList<ProductResponse>> map = new HashMap<>();

        categories.forEach(category -> {
            ArrayList<ProductResponse> productResponses = new ArrayList<>();
            map.put(category.getCategoryName(), productResponses);
        });

        products.forEach(product -> {
            ProductResponse response= modelMapper.map(product, ProductResponse.class);
            map.get(product.getCategory().getCategoryName()).add(response);
        });

        for(String category: map.keySet()) {
            ShopProductResponse response = new ShopProductResponse();
            response.setCategory(category);
            response.setProducts(map.get(category));
            shopProductResponses.add(response);
        }
        return shopProductResponses;
    }

    @Override
    public MessageResponse addNewProduct(ProductRequest productRequest) {
        return null;
    }

    @Override
    public MessageResponse updateProduct(ProductRequest productRequest) {
        return null;
    }

    @Override
    public MessageResponse deleteByProductId(Long productId) {
        return null;
    }

    @Override
    public List<ProductResponse> findAllBySellerId(String sellerId) {
        return null;
    }
}
