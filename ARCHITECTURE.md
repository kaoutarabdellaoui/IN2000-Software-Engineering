# Introduksjon

Dette dokumentet er ment for utviklere som skal vedlikeholde,
videreutvikle eller sette seg inn i appen.
Det gir en oversikt over arkitekturen som brukes i prosjektet.

# Arkitektur oversikt

Appen er bygget med bruk av MVVM (Model-View-ViewModel) designmønsteret,
kombinert med prinsipper fra UDF (Unidirectional Data Flow).

# Hovedprinsipper

- Lav kobling: Klasser og komponenter er designet for å minimere avhengigheter.
- Høy kohesjon: Hver klasse eller modul har ett enkelt, godt definert ansvar.

# Designmønstre

## MVVM:

- Model: Håndterer data. Dette inkluderer API-kall og lagring.
- View: Viser data til brukeren og sender brukerinteraksjoner videre til ViewModel.
- ViewModel: Fungerer som en mellommann mellom View og Model, eksponerer data og håndterer
  brukerhandlinger.

## UDF:

- Sikrer en forventet dataflyt ved å bruke uforanderlig tilstand og handlinger for å oppdatere
  tilstanden. Denne tilnærmingen forenkler feilsøking og testing.

# Prosjektstruktur

## Prosjektet er organisert i følgende hovedpakker:

- ui: Inneholder UI-komponenter og skjermer.
- data: Håndterer datakilder, repositories og modeller.

# Eksempler

## Datalag:

- HikeAPIDatasource.kt: Henter data fra Hike API.
- HikeAPIRepository.kt: Tilbyr et rent API for å få tilgang til data.

## UI-lag:

- MapViewer.kt: Viser kartrelatert data til brukeren.

## ViewModel-lag:

- OpenAIViewModel.kt: Håndterer logikk og tilstand knyttet til chatbot-funksjonalitet.

# Brukte teknologier

- Kotlin: Hovedprogrammeringsspråket.
- Gradle: Byggesystem.
- Ktor: For HTTP-klientoperasjoner.
- Mapbox: For kartvisning og håndtering av geodata.
- Jetpack Compose: For bygging av UI-komponenter.
- Kotlinx Serialization: For JSON-serialisering og -deserialisering.
- Og mange fler

# Objektorienterte prinsipper

- Innkapsling: Data og oppførsel er innkapslet i klasser og eksponerer kun nødvendige grensesnitt.
- Arv: Brukes med forsiktighet for å unngå tett kobling, og sammensetning foretrekkes fremfor arv.
- Polymorfisme: Grensesnitt og abstrakte klasser brukes for å tillate fleksibilitet og utvidbarhet.

# API-nivå

Vi bruker API nivå 35, vi har valgt å bruke dette API nivået fordi det er den nyeste versjonen som
ikke er i beta.
Vi har da tilgang til de nyeste funksjonene, bedre ytelse i tilegg til forbedret sikkerhet.

# Vedlikehold og videreutvikling

- Følg de eksisterende MVVM- og UDF-mønstrene for å sikre konsistens.
- Skriv enhetstester for ViewModel- og Repository-lagene for å sikre pålitelighet.
- Dokumentér eventuelle nye arkitektoniske beslutninger i denne filen.