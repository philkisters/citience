/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */
package org.citience.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.drasyl.identity.DrasylAddress;
import org.drasyl.identity.IdentityPublicKey;

public interface IdentityPublicKeyMixin {
    @JsonValue
    String toString();

    @JsonCreator
    static DrasylAddress of(final String bytes) {
        return IdentityPublicKey.of(bytes);
    }
}

