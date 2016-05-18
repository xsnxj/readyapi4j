package com.smartbear.readyapi.client.teststeps.datasource.datagen;

import com.smartbear.readyapi.client.model.DataGenerator;

abstract class AbstractDataGeneratorBuilder<BuilderType> {
    private final String property;
    private int duplicationFactor;

    public AbstractDataGeneratorBuilder(String property) {
        this.property = property;
    }

    public BuilderType duplicatedBy(int duplicationFactor) {
        this.duplicationFactor = duplicationFactor;
        return (BuilderType) this;
    }

    public DataGenerator build() {
        DataGenerator dataGenerator = createDataGenerator();
        dataGenerator.setPropertyName(property);
        dataGenerator.setDuplicationFactor(duplicationFactor);
        return dataGenerator;
    }

    protected abstract DataGenerator createDataGenerator();
}
