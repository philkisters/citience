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

public record DrasylAddress(IdentityPublicKey address) implements Address {
    @JsonCreator
    public DrasylAddress(@JsonProperty("address") final IdentityPublicKey address) {

        this.address = address;
    }

    @JsonIgnore
    @Override
    public String getSerializedAddress() {
        return address.toString();
    }

    @Override
    public String toString() {
        return "DrasylAddress{" +
                "address=" + address.toString() +
                '}';
    }
}
