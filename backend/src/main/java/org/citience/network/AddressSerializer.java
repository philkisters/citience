/*
 * Copyright © 2021 Philipp Kisters
 * This source code is licensed under MIT.
 * Full license can found in the "/LICENSE"-File or at https://opensource.org/licenses/MIT
 */

package org.citience.network;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drasyl.identity.IdentityPublicKey;
import org.drasyl.node.handler.serialization.Serializer;
import org.skabnet.messages.AttributeSearchWrapper;
import org.skabnet.messages.DiscoveryMessage;
import org.skabnet.messages.NotFoundMessage;
import org.skabnet.peer.info.AttributeBasedNameId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A serializer based on the Jackson-Json serializer provided by drasyl.
 */
public class AddressSerializer implements Serializer {
    private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();

    public AddressSerializer() {
        JACKSON_MAPPER.registerSubtypes(DrasylAddress.class);
        JACKSON_MAPPER.addMixIn(IdentityPublicKey.class, IdentityPublicKeyMixin.class);
        JACKSON_MAPPER.registerSubtypes(AttributeSearchWrapper.class, DiscoveryMessage.class, NotFoundMessage.class, AttributeBasedNameId.class);

        JACKSON_MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @Override
    public byte[] toByteArray(final Object o) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JACKSON_MAPPER.writeValue(out, o);
            return out.toByteArray();
        }
    }

    @Override
    public <T> T fromByteArray(final byte[] bytes, final Class<T> type) throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            return JACKSON_MAPPER.readValue(in, type);
        }
    }
}
