/*
 * Copyright 2014 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reinert.requestor.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reinert.requestor.serialization.json.JsonStringSerdes;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of {@link JsonStringSerdes}.
 */
public class JsonStringSerdesTest {

    private final JsonStringSerdes serdes = JsonStringSerdes.getInstance();

    @Test
    public void deserializeCollection() throws Exception {
        // Set-up mock
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        Mockito.when(context.getInstance(List.class)).thenReturn(new ArrayList());

        String input = "[\"some\",\"any\"]";
        Collection<String> expected = Arrays.asList("some", "any");

        @SuppressWarnings("unchecked")
        Collection<String> output = serdes.deserialize(List.class, input, context);

        assertEquals(expected, output);
    }

    @Test
    public void deserializeValue() throws Exception {
        assertEquals("some", serdes.deserialize("\"some\"", null));
    }

    @Test
    public void serializeCollection() throws Exception {
        Collection<String> input = Arrays.asList("some", "any");
        String expected = "[\"some\",\"any\"]";

        String output = serdes.serialize(input, null);

        assertEquals(expected, output);
    }

    @Test
    public void serializeValue() throws Exception {
        assertEquals("\"some\"", serdes.serialize("some", null));
    }
}
