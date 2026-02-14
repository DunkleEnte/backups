package dev.dunkleente.database;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InvestData {

    private final UUID uuid;
    private double investedMoney;
    private double generatedMoney;
    private boolean autoCollect;
    private double totalGeneratedMoney;

    public InvestData(UUID uuid, double investedMoney, double generatedMoney, boolean autoCollect, double totalGeneratedMoney) {
        this.uuid = uuid;
        this.investedMoney = investedMoney;
        this.generatedMoney = generatedMoney;
        this.autoCollect = autoCollect;
        this.totalGeneratedMoney = totalGeneratedMoney;
    }

    public void addGeneratedMoney(double amount) {
        this.generatedMoney += amount;
        this.totalGeneratedMoney += amount;
    }

    public void reset() {
        this.investedMoney = 0.0;
        this.generatedMoney = 0.0;
        this.totalGeneratedMoney = 0.0;
    }
}
