# GATOS TV — projeto Android inicial

Projeto-base em Kotlin + Jetpack Compose para Android celular, Android TV e TV Box.

## O que já está incluído
- Estrutura Android Studio / Gradle.
- Tela inicial de conexão Xtream Codes.
- Validação de servidor, usuário e senha via `player_api.php`.
- Interface escura adaptável a telas grandes.
- Permissão de internet e entrada Android TV.

## Abrir
1. Instale o Android Studio (versão compatível com Android Gradle Plugin 8.7.3).
2. Abra a pasta `GATOS-TV` no Android Studio.
3. Aguarde a sincronização Gradle.
4. Conecte um dispositivo Android ou inicie um emulador.
5. Use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Próximas etapas
- Persistência segura das credenciais (Android Keystore).
- Carregar categorias e listas de TV, filmes e séries.
- Reprodução HLS/MP4 com AndroidX Media3.
- EPG, favoritos, busca e histórico.
- Ajustes de foco e navegação por controle remoto.
- Ícone e arte finais da marca.

## Observações
Este é um ponto de partida do código-fonte, não um APK final. O APK precisa ser compilado e testado no Android Studio. Use somente listas e conteúdos que você tenha autorização para acessar e distribuir. O app não fornece listas, canais ou credenciais.
