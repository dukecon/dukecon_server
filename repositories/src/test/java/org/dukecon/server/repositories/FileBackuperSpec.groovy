package org.dukecon.server.repositories

import spock.lang.Specification

class FileBackuperSpec extends Specification {

    private String destDirName

    void setup() {
        def random = new Random()
        destDirName = "target/testoutput/FileBackuperSpec_${random.nextInt(1000)}"
        File destDir = new File(destDirName)
        destDir.mkdirs()
        assert destDir.exists()
    }

    void "Backup text content in a file"() {
        when:
        File file = FileBackuper.of('some content', destDirName, 'some_content.txt')

        then:
        assert file.exists()
        assert file.getAbsolutePath().contains(destDirName.tokenize('/').last())
        assert file.text == 'some content'
    }

    void "Backup web url in a file"() {
        when:
        File file = FileBackuper.of('https://www.dukecon.org'.toURL(), destDirName, 'dukecon.org.html')

        then:
        assert file.exists()
        assert file.getAbsolutePath().contains(destDirName.tokenize('/').last())
        assert file.text.contains('DukeCon Project')
    }

    void "Invalid web url won't backup a file"() {
        when:
        File file = FileBackuper.of('https://www.dukecon-gibts-nicht.org'.toURL(), destDirName, 'gibtsnicht.html')

        then:
        assert !(file?.exists())
    }
}
