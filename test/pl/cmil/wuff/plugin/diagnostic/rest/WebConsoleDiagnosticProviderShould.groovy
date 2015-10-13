package pl.cmil.wuff.plugin.diagnostic.rest

import pl.cmil.wuff.plugin.diagnostic.BundleDiagnosis
import pl.cmil.wuff.plugin.diagnostic.RawDiagnosticDataProvider
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.util.concurrent.Executors

class WebConsoleDiagnosticProviderShould extends Specification {
    @Shared
            executor = Executors.newSingleThreadExecutor()

    static final pass = "admin";
    static final user = "admin"

    static final RawDiagnosticDataProvider allGoodDataProvider = { Files.getResource("/test-bundle-list.json").text }
    static final RawDiagnosticDataProvider smallDataProvider = { Files.getResource("/small-bundle-list.json").text }


    def "get all bundles from the application"() {
        setup:
        def obj = new WebConsoleDiagnosticProvider(allGoodDataProvider, executor);

        when:
        def diagnosis = obj.diagnose().get();

        then:
        diagnosis.size() == 86
    }

    def "get proper ids of the bundles"() {
        setup:
        def obj = new WebConsoleDiagnosticProvider(smallDataProvider, executor);
        def diagnosis = obj.diagnose().get();

        expect:
        diagnosis.get(index).getId() == id

        where:
        index << [0, 1, 2]
        id << [0, 3, 4]
    }


    def "get proper status"() {
        setup:
        def obj = new WebConsoleDiagnosticProvider(smallDataProvider, executor);
        def diagnosis = obj.diagnose().get();

        expect:
        diagnosis.get(index).getStatus() == status

        where:
        index << [0, 1, 2]
        status << [BundleDiagnosis.Status.OK, BundleDiagnosis.Status.OK, BundleDiagnosis.Status.NOT]
    }


}
