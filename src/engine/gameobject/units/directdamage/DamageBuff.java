package engine.gameobject.units.directdamage;

import java.util.Optional;
import engine.fieldsetting.Settable;
import engine.gameobject.GameObject;
import engine.gameobject.units.Buff;
import engine.gameobject.units.UpgradeType;
import engine.gameobject.weapon.Upgrade;



/**
 * Creates buff that does damage, @param increment is damage
 * @author Danny Oh and Nathan Prabhu
 *
 */
public class DamageBuff extends Buff implements DirectDamage {

    private double increment;
    private Optional<DirectDamage> decorated;
    private final static double graphicDuration = .33;

    public DamageBuff () {
        super(graphicDuration);
        this.increment = 0;
        decorated = Optional.empty();
    }

    public DamageBuff (double increment) {
        super(graphicDuration);
        this.increment = increment;
        decorated = Optional.empty();
    }

    @Settable
    public void setIncrement (double increment) {
        this.increment = increment;
    }

    @Override
    public void apply (GameObject myUnit) {
        myUnit.changeHealth(-1 * getDamage());
        //System.out.println(getDamage());
        adjustEffect(myUnit, -1, 1, 0, 0);
    }

    @Override
    public double getDamage () {
        return decorated.map(this::getIncrementedDamage).orElse(increment);
    }

    private double getIncrementedDamage (DirectDamage sublayer) {
        return sublayer.getDamage() + increment;
    }

    @Override
    public void unapply (GameObject myUnit) {
        adjustEffect(myUnit, 1, -1, 0, 0);
    }

    /*
     * You always want the new damage to apply, so return true
     */
    @Override
    public boolean isStrongerBuff (Buff otherBuff) {
        return true;
    }

    @Override
    public void upgrade (Upgrade decorated) {
        this.decorated = Optional.of((DirectDamage) decorated);
    }

    @Override
    public Buff clone () {
        return new DamageBuff(getDamage());
    }

}
