package org.dukecon.server.convert


import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class GenerateDukeconTest {
    @Test
    void testMain() {
        GenerateDukecon.main(['conferences-javaland.yml', '../resources/src/main/resources'] as String[])

        String outputDir = 'htdocs/rest/javaland/2019/'
        assert new File(outputDir).exists()
        assert new File(outputDir + 'img/favicon.ico').exists()
        assert new File(outputDir + 'styles.css').exists()
        assert new File(outputDir + 'rest/conferences/javaland2019.json').exists()
        new File(outputDir + 'rest/speaker/images').with {
            assert it.isDirectory()
            assert !it.list().toList().isEmpty()
        }
        assert new File(outputDir + 'rest/styles.css').exists()
        assert new File(outputDir + 'rest/init.json').exists()
        assert new File(outputDir + 'rest/image-resources.json').exists()

        // TODO: test for keys in json files
    }


}
