package org.dukecon.server.util

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ResourcesFinderTests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    def filesToTest = ['1.jpg', '10.jpg', '11.jpg', '12.jpg', '13.jpg', '14.jpg', '15.jpg', '2.jpg', '3.jpg', '4.jpg', '5.jpg', '6.jpg', '7.jpg', '8.jpg', '9.jpg']

    @Before
    void init() {
        folder.newFolder("streams")
        filesToTest.forEach { file ->
            folder.newFile("streams"+ File.separator + file)
        }
    }

    @Test
    public void listFilesInClasspathFolder() {
        // when
        def resourcesFinder = new ResourcesFinder(folder.root.getAbsolutePath() + File.separator + "streams")

        // and
        def listOfFiles = resourcesFinder.getFileList()

        // and
        def category = resourcesFinder.category

        // then
        assert listOfFiles.isPresent()

        // and
        assert listOfFiles.get().size() == 15

        // and
        assert listOfFiles.get().values().name.sort() == filesToTest

        // and
        assert category == 'streams'
    }

    @Test
    public void listFilesInNotExistingClasspathFolder() {
        // when
        def resourcesFinder = new ResourcesFinder(folder.root.getAbsolutePath()+"/whatever")

        // and
        def listOfFiles = resourcesFinder.getFileList()

        // and
        def category = resourcesFinder.category

        // then
        assert !listOfFiles.isPresent()

        // and
        assert category == 'whatever'
    }
}
