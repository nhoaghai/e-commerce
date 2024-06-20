package com.ecommerce.domain.product.serviceImpl;

import com.ecommerce.common.util.MessageResponse;
import com.ecommerce.common.util.PageResponseDto;
import com.ecommerce.domain.product.dto.request.ProductRequest;
import com.ecommerce.domain.product.dto.response.ProductResponse;
import com.ecommerce.domain.product.exception.CategoryException;
import com.ecommerce.domain.product.model.Product;
import com.ecommerce.domain.product.repository.CategoryRepository;
import com.ecommerce.domain.product.repository.ProductRepository;
import com.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

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
            throw CategoryException.notFound("No product found matching the search criteria");
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
            throw CategoryException.notFound("No product found matching the categoryId");
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
            throw new CategoryException("Not found product with sku");
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
            throw new CategoryException("Not found product with id");
        }else {
            ProductResponse response = modelMapper.map(product, ProductResponse.class);
            response.setCategoryName(product.getCategory().getCategoryName());
            response.setShopName(product.getSeller().getShopName());
            return response;
        }
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
