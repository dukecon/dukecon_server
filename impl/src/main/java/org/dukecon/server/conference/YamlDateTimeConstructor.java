package org.dukecon.server.conference;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public class YamlDateTimeConstructor extends Constructor {
    public YamlDateTimeConstructor() {
        this.yamlConstructors.put(Tag.TIMESTAMP, new SafeConstructor.ConstructYamlTimestamp() {
            @Override
            public Object construct(Node node) {
                return ((Date) super.construct(node)).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        });
    }
}
