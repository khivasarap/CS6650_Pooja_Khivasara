# ItemsApi

All URIs are relative to *https://virtserver.swaggerhub.com/gortonator/GianTigle/1.11*

Method | HTTP request | Description
------------- | ------------- | -------------
[**topItems**](ItemsApi.md#topItems) | **GET** /items/store/{storeID} | get top 10 items sold at store
[**topStores**](ItemsApi.md#topStores) | **GET** /items/top10/{itemID} | get top 5 stores for item sales

<a name="topItems"></a>
# **topItems**
> TopItems topItems(storeID)

get top 10 items sold at store

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ItemsApi;


ItemsApi apiInstance = new ItemsApi();
Integer storeID = 56; // Integer | ID of the store for top 10 sales
try {
    TopItems result = apiInstance.topItems(storeID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ItemsApi#topItems");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **storeID** | **Integer**| ID of the store for top 10 sales |

### Return type

[**TopItems**](TopItems.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="topStores"></a>
# **topStores**
> TopStores topStores(itemID)

get top 5 stores for item sales

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ItemsApi;


ItemsApi apiInstance = new ItemsApi();
Integer itemID = 56; // Integer | ID of the items that we want to find top stores
try {
    TopStores result = apiInstance.topStores(itemID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ItemsApi#topStores");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **itemID** | **Integer**| ID of the items that we want to find top stores |

### Return type

[**TopStores**](TopStores.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

