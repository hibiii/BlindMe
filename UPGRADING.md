# Steps to upgrade BlindMe to a new version of Minecraft

1. Update Loom. If necessary, upgrade Gradle wrapper with `./gradlew wrapper --gradle-version M.N`.
2. Update dependency versions.
3. Fix errors in code.
4. Run tests: client screens, client gameplay, server-modded, server-vanilla.
5. If behavior deviates, run tests again.
6. Update mod loader manifests.
