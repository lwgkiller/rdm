package com.redxun.core.elastic;


import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author ferhat
 * @author cihat keser
 */
public class CreateMapping extends GenericResultAbstractAction {

    protected CreateMapping(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.payload = builder.source;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) ;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<CreateMapping, Builder> {
        private String index;
        private Object source;

        public Builder(String index,  Object source) {
            this.index = index;
            this.source = source;
        }

        @Override
        public CreateMapping build() {
            return new CreateMapping(this);
        }
    }

}
