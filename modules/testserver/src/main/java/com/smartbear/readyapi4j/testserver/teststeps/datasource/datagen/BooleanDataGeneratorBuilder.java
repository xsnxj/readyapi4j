package com.smartbear.readyapi4j.testserver.teststeps.datasource.datagen;

import com.smartbear.readyapi.client.model.BooleanDataGenerator;
import com.smartbear.readyapi.client.model.DataGenerator;

public class BooleanDataGeneratorBuilder extends AbstractDataGeneratorBuilder<BooleanDataGeneratorBuilder> {

    private final BooleanDataGenerator booleanDataGenerator = new BooleanDataGenerator();

    BooleanDataGeneratorBuilder(String property) {
        super(property);
        booleanDataGenerator.setType("Boolean");
        booleanDataGenerator.setFormat(BooleanDataGenerator.FormatEnum.TRUE_FALSE);
    }

    BooleanDataGeneratorBuilder withYesNoFormat() {
        booleanDataGenerator.setFormat(BooleanDataGenerator.FormatEnum.YES_NO);
        return this;
    }

    BooleanDataGeneratorBuilder withDigitsFormat() {
        booleanDataGenerator.setFormat(BooleanDataGenerator.FormatEnum._1_0);
        return this;
    }

    @Override
    protected DataGenerator buildDataGenerator() {
        return booleanDataGenerator;
    }
}
