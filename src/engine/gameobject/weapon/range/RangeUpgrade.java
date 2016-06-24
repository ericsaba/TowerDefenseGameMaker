package engine.gameobject.weapon.range;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import engine.fieldsetting.Settable;
import engine.gameobject.units.UpgradeType;
import engine.gameobject.weapon.Upgrade;
import engine.observable.Observer;


/**
 * Manages a weapon's range. It is both an upgrade and an upgradable via the decorator pattern.
 *
 * @author Nathan Prabhu
 *
 */

public class RangeUpgrade implements Range, Upgrade {

    private List<Observer> observers = new ArrayList<>();
    private double increment;
    private Optional<Range> decorated;
    private UpgradeType type;

    public RangeUpgrade () {
        this(0);
    }

    public RangeUpgrade (double increment) {
        setIncrement(increment);
        setType(UpgradeType.NULL);
        decorated = Optional.empty();
    }

    @Settable
    public void setIncrement (double increment) {
        this.increment = increment;
    }

    private void setType (UpgradeType type) {
       this.type = type;
    }

    @Override
    public UpgradeType getType () {
        return type;
    }

    @Override
    public double getRange () {
        return decorated.map(this::getIncrementedRange).orElse(increment);
    }

    private double getIncrementedRange (Range sublayer) {
        return sublayer.getRange() + increment;
    }

    @Override
    public void upgrade (Upgrade decorated) {
        this.decorated = Optional.of((Range) decorated);
    }

    public Upgrade clone () {
        return new RangeUpgrade(increment);
    }

    
    @Override
    public void addObserver (Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver (Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers () {
        observers.forEach(obs -> obs.update());
    }

}
