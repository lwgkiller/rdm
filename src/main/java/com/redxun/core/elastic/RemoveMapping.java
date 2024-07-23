package com.redxun.core.elastic;


import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * @author ferhat
 * @author cihat keser
 */
public class RemoveMapping extends GenericResultAbstractAction {

    protected RemoveMapping(Builder builder) {
        super(builder);
        this.indexName = builder.index;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) ;
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<RemoveMapping, Builder> {
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        @Override
        public RemoveMapping build() {
            return new RemoveMapping(this);
        }
    }

}
