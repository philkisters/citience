package org.citience.models;


import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "configs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "module")
        })
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "module")
    private String module;

    @Column(name = "parameter", length = 10000)
    private String parameter;

    @Transient
    private Map<String, String> parameterMap;

    public Configuration() {
        parameterMap = new HashMap<>();
    }


    public Configuration(final String module) {
        this.parameter = "";
        this.module = module;
        this.parameterMap = new HashMap<>();
    }

    private void syncParameters() {
        Map<String, String> parsedParameters = parametersToMap();

        parsedParameters.putAll(parameterMap);
        parameterMap.putAll(parsedParameters);

        this.parameter = mapToParameterString();
    }

    private Map<String, String> parametersToMap() {
        HashMap<String, String> result = new HashMap<>();

        if (this.parameter != null && !this.parameter.isBlank()) {
            String[] parameterPairs = this.parameter.split(";");

            for (String pair: parameterPairs) {
                if (pair.contains(":")) {
                    String[] par = pair.split(":");
                    result.put(par[0], par.length == 2 ? par[1] : "");
                }
            }
        }

        return result;
    }

    private String mapToParameterString () {
        StringBuilder sb = new StringBuilder();

        parameterMap.forEach((parameterName, parameterValue) -> {
            sb.append(parameterName);
            sb.append(":");
            sb.append(parameterValue);
            sb.append(";");
        });

        return sb.toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getParameter(final String parameterName) {
        this.syncParameters();
        return this.parameterMap.get(parameterName);
    }

    public void addParameter(final String parameterName, final String parameterValue) {
        this.parameterMap.put(parameterName, parameterValue);
        this.syncParameters();
    }
}
