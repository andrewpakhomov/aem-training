/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps.training.components.searchcomponent;

import com.epam.aem.training.core.searchservice.SearchApiType;
import com.epam.aem.training.core.searchservice.SearchResultViewModel;
import com.adobe.cq.sightly.WCMUse;
import com.epam.aem.training.core.searchservice.AbstractSearchService;
import com.epam.aem.training.core.searchservice.SearchServiceFactory;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;

/**
 *
 * @author Andrey_Pakhomov
 */
public class SearchComponent extends WCMUse{
    
    private final static Map<SearchApiType,String> searchApiTypeToStringMapper = new EnumMap<SearchApiType, String>(SearchApiType.class);
    
    static{
        searchApiTypeToStringMapper.put(SearchApiType.QUERY_MANAGER, "qm");
        searchApiTypeToStringMapper.put(SearchApiType.QUERY_BUILDER_API, "qb");
    }
    
    private static String mapApiTypeToPropertyValue(SearchApiType apiType){
        for (Map.Entry<SearchApiType, String> current : searchApiTypeToStringMapper.entrySet()){
            if (Objects.equals(apiType, current.getKey())) return current.getValue();
        }
        //default
        return "qm";
    }
    
    private static SearchApiType mapPropertyValueToApiType(String value){
        for (Map.Entry<SearchApiType, String> current : searchApiTypeToStringMapper.entrySet()){
            if (Objects.equals(value, current.getValue())) return current.getKey();
        }
        //default;
        return SearchApiType.QUERY_BUILDER_API;
    }
    
    
    private final static String TEXT_TO_SEARCH_KEY = "texttosearch";
    
    private final static String SEARCH_API_TYPE_KEY = "searchapi";
    
    private final static String SEARCH_PATHS_KEY = "searchpaths";
    
    
    private List<String> searchPaths;
    
    private SearchApiType searchApiType;
    
    private String searchText;
    
    private AbstractSearchService searchService;
    
    private List<SearchResultViewModel> results;

    public SearchComponent() {
        
    }
   
    @Override
    public void activate() throws Exception {
        Resource resource = this.getResource();
        ValueMap properties = resource.getValueMap();
        this.searchPaths = Arrays.asList(PropertiesUtil.toStringArray(properties.get(SEARCH_PATHS_KEY), new String[]{""}));
        this.searchApiType = mapPropertyValueToApiType(properties.get(SEARCH_API_TYPE_KEY, SearchApiType.QUERY_BUILDER_API.toString()));
        this.searchText = properties.get(TEXT_TO_SEARCH_KEY, "");
        SearchServiceFactory searchServiceFactory = this.getSlingScriptHelper().getService(SearchServiceFactory.class);
        this.searchService = searchServiceFactory.getSearchService(searchPaths, searchText, searchApiType,this.getResourceResolver());
        this.results = this.searchService.search();
    }
    
    public List<SearchResultViewModel> getSearchResults(){
        return this.results;
    }
    
    public String getSearchText(){
        return this.searchText;
    }
    
    public List<String> getSearchPaths(){
        return this.searchPaths;
    }
    
    public String getSearchApiType(){
        return mapApiTypeToPropertyValue(this.searchApiType);
    }

}
