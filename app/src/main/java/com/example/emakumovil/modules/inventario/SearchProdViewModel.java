package com.example.emakumovil.modules.inventario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.SearchQuery;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class SearchProdViewModel extends ViewModel {

    private final MutableLiveData<List<FieldsDDV>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<List<FieldsDDV>> getProducts() {
        return products;
    }
    public void clearProducts() {
        products.setValue(new ArrayList<>());
    }
    public LiveData<Boolean> isLoading() {
        return isLoading;
    }
    int contador = 0;
    public void searchProducts(String query, final String queryCode) {
        isLoading.setValue(true);
        //products.setValue(new ArrayList<>()); // Clear previous results


        // This listener will handle the result from the background SearchQuery
        AnswerListener listener = new AnswerListener() {
            @Override
            public void arriveAnswerEvent(AnswerEvent e) {
                System.out.println("se ejecuto el listener ..."+contador);
                contador++;
                if (queryCode.equals(e.getSqlCode())) {
                    Document doc = e.getDocument();
                    Element elm = doc.getRootElement();
                    List<Element> listRows = elm.getChildren("row");
                    ArrayList<FieldsDDV> results = new ArrayList<>();
                    for (Element row : listRows) {
                        System.out.println("cargando resultado de query ...");
                        List<Element> cols = row.getChildren();
                        String descripcion1 = cols.get(0).getText();
                        String descripcion2 = cols.get(1).getText();
                        System.out.println("values ..."+descripcion1);
                        results.add(new FieldsDDV(descripcion1, descripcion2, ""));
                    }
                    products.postValue(results); // Use postValue from a background thread
                }
                isLoading.postValue(false);
            }

            @Override
            public boolean containSqlCode(String sqlCode) {
                return queryCode.equals(sqlCode);
            }
        };

        // Start the background search
        new SearchQuery(listener, queryCode, new String[]{query}).start();
    }
}
