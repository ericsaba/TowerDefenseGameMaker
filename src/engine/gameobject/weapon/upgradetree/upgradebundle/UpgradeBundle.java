package engine.gameobject.weapon.upgradetree.upgradebundle;

import engine.gameobject.Purchasable;
import engine.gameobject.weapon.Upgrade;
import engine.gameobject.weapon.UpgradeSet;
import engine.gameobject.weapon.upgradetree.UpgradeTree;


/**
 * A predetermined method of upgrading a weapon's Upgrades. UpgradeBundles will be purchased at
 * the shop, and so they extend Purchasable. A weapon accesses its appropriate UpgradeBundles
 * through its UpgradeTree.
 *
 * @author Nathan Prabhu
 *
 */
public interface UpgradeBundle extends Purchasable<UpgradeBundle> {

    /**
     * Upgrades the existing upgrades and replaces them in the map. If the current type of
     * upgrade doesn't exist in the map, it is added as a new entry to the map.
     *
     * @param upgradables
     */
    public void applyUpgrades (UpgradeSet<Upgrade> upgradables);

    /**
     *
     * @return whether or not this upgrade is the last child in an UpgradeTree.
     */
    public boolean isFinalUpgrade ();

    /**
     *
     * @return parent UpgradeTree
     */
    public UpgradeTree getParent ();

}
