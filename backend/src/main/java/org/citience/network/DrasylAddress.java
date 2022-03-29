/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */
package org.citience.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.drasyl.identity.IdentityPublicKey;
import org.skabnet.info.Address;

public class DrasylAddress implements Address {
    private final IdentityPublicKey drasylAddress;

    @JsonCreator
    public DrasylAddress(@JsonProperty("drasylAddress") final IdentityPublicKey drasylAddress) {

        this.drasylAddress = drasylAddress;
    }

    public IdentityPublicKey getDrasylAddress() {
        return drasylAddress;
    }

    @JsonIgnore
    @Override
    public String getSerializedAddress() {
        return drasylAddress.toString();
    }

    @Override
    public String toString() {
        return "DrasylAddress{" +
                "drasylAddress=" + drasylAddress.toString() +
                '}';
    }
}
