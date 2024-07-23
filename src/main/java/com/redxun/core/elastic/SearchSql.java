package com.redxun.core.elastic;

import java.util.Objects;

import com.google.gson.Gson;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.core.SearchResult;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class SearchSql extends AbstractAction<JestResult> {

    private String query;


    protected SearchSql(Builder builder) {
        
        this.query = builder.query;
    }


    @Override
    public JestResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new SearchResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return  "/_xpack/sql?format=json" ;
    }

    @Override
    public String getPathToResult() {
        return "rows";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getData(Gson gson) {
           return this.query;
    }

 

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), query);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        SearchSql rhs = (SearchSql) obj;
        return super.equals(obj)
                && Objects.equals(query, rhs.query)
              ;
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<SearchSql, Builder> {
        protected String query;
    

        public Builder(String query) {
            this.query = query;
        }

        @Override
        public SearchSql build() {
            return new SearchSql(this);
        }
    }

  
}
