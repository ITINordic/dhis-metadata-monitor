/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import java.util.Collection;
import java.util.function.Function;
import lombok.experimental.Delegate;

/**
 *
 * @author cliffordc
 */

/*
final HttpResponse<String> result = Unirest.get("https://zim.dhis2.org/develop/api/{type}/{id}")
        .routeParam("type", "categories")
        .routeParam("id", "ru80fGU70hD")
        .queryString("fields", "categoryOptions[name,code,id]")
        .basicAuth(prop.getProperty("username"), prop.getProperty("password"))
        .asString();
return result.getBody();
 */
public class DHISQueryBuilder extends DHISQuery.DHISQueryBuilder {

    @Override
    public DHISQueryBuilder clearFields() {
        return (DHISQueryBuilder) super.clearFields(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DHISQueryBuilder field(Field field) {
        return (DHISQueryBuilder) super.field(field); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DHISQueryBuilder accept(String accept) {
        return (DHISQueryBuilder) super.accept(accept); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DHISQueryBuilder id(String id) {
        return (DHISQueryBuilder) super.id(id); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DHISQueryBuilder type(String type) {
        return (DHISQueryBuilder) super.type(type); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public DHISQueryBuilder url(String url) {
        return (DHISQueryBuilder) super.url(url); //To change body of generated methods, choose Tools | Templates.
    }

    public DHISFieldBuilder<DHISQueryBuilder> beginField() {
        return DHISFieldBuilder.begin(f -> (DHISQueryBuilder) this.field(f));
    }

    public static DHISQueryBuilder builder() {
        DHISQueryBuilder builder;
        builder = new DHISQueryBuilder();
        return builder;
    }

    public static class DHISFieldBuilder<T> extends Field.FieldBuilder {

        @Override
        public DHISFieldBuilder<T> clearFields() {
            return (DHISFieldBuilder<T>) super.clearFields(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DHISFieldBuilder<T> fields(Collection<? extends Field> fields) {
            return (DHISFieldBuilder<T>) super.fields(fields); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DHISFieldBuilder<T> field(Field field) {
            return (DHISFieldBuilder<T>) super.field(field); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public DHISFieldBuilder<T> name(String name) {
            return (DHISFieldBuilder<T>) super.name(name); //To change body of generated methods, choose Tools | Templates.
        }

        private static <T> DHISFieldBuilder<T> begin(Function<Field, T> callback) {
            DHISFieldBuilder<T> builder = new DHISFieldBuilder<>();
            builder.callback = callback;
            return builder;
        }

        public DHISFieldBuilder<DHISFieldBuilder<T>> beginField() {
            return DHISFieldBuilder.begin(f -> (DHISFieldBuilder<T>) this.field(f));
        }
        private Function<Field, T> callback;

        public T end() {
            return callback.apply(build());
        }

    }
}

