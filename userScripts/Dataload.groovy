def env = System.getenv()
def service = ctx.getBean('providerService')
def dataHome = env['DATADIR'] ?: "/opt"

boolean shaMatches(String shaName, String fileName) {
    def shaFile = new File(shaName)
    if (!shaFile.exists()) {
        return false
    }
    def fileSha = shaFile.text
    def trueSha = "sha1sum $fileName | cut -d ' ' -f 1".execute().inputStream.text.split(' ')[0]
    return fileSha == trueSha
}

def updateSha(String shaName, String fileName) {
    def shaFile = new File(shaName)
    def trueSha = "sha1sum $fileName | cut -d ' ' -f 1".execute().inputStream.text.split(' ')[0]
    shaFile.text = trueSha
}

if (!shaMatches("data/providers.sha1", "$dataHome/NPI/small.csv")) {
    service.loadData("$dataHome/NPI/small.csv")
    updateSha("data/providers.sha1", "$dataHome/NPI/small.csv")
}
