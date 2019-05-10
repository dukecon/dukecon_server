package org.dukecon.server.util

import org.dukecon.model.Resources
import org.junit.Test

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ResourcesFinderTests {

    @Test
    public void listFilesInClasspathFolder() {
        // given
        def classpathFolder = 'img/javaland2019/streams'

        // when
        def resourcesFinder = new ResourcesFinder(classpathFolder)

        // and
        def listOfFiles = resourcesFinder.getFileList()

        // and
        def category = resourcesFinder.category

        // then
        assert listOfFiles.isPresent()

        // and
        assert listOfFiles.get().size() == 15

        // and
        assert listOfFiles.get().values().name.sort() == ['1.jpg', '10.jpg', '11.jpg', '12.jpg', '13.jpg', '14.jpg', '15.jpg', '2.jpg', '3.jpg', '4.jpg', '5.jpg', '6.jpg', '7.jpg', '8.jpg', '9.jpg']

        // and
        assert category == 'streams'
    }

    @Test
    public void listFilesInNotExistingClasspathFolder() {
        // given
        def classpathFolder = 'img/javaland2019/nil'

        // when
        def resourcesFinder = new ResourcesFinder(classpathFolder)

        // and
        def listOfFiles = resourcesFinder.getFileList()

        // and
        def category = resourcesFinder.category

        // then
        assert !listOfFiles.isPresent()

        // and
        assert category == 'nil'
    }
}