package org.dukecon.server.repositories

import spock.lang.Specification

class FileBackuperSpec extends Specification {

    private String destDir

    void setup() {
        def random = new Random()
        destDir = "target/testoutput/FileBackuperSpec_${random.nextInt(1000)}"
        assert !new File(destDir).exists()
    }

    void "Backup text content in a file"() {
        when:
        def file = FileBackuper.of('some content', destDir, 'some_content.txt')

        then:
        assert file.exists()
        assert file.getAbsolutePath().contains(destDir.tokenize('/').last())
        assert file.text == 'some content'
    }

    void "Backup web url in a file"() {
        when:
        def file = FileBackuper.of('http://www.dukecon.org'.toURL(), destDir, 'dukecon.org.html')

        then:
        assert file.exists()
        assert file.getAbsolutePath().contains(destDir.tokenize('/').last())
        assert file.text.contains('DukeCon Project')
    }
    void "Invalid web url won't backup a file"() {
        when:
        def file = FileBackuper.of('http://www.dukecon-gibts-nicht.org'.toURL(), destDir, 'gibtsnicht.html')

        then:
        assert !file
    }
}
