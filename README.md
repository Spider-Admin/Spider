# Spider

Spider crawls freesites in [Freenet](https://freenetproject.org/), extracts various information and creates an index of these freesites. It is currently used by me to create my uncensored index [Spider](http://localhost:8888/USK@nLTpFO0hKAp9AaaChDzk~hA95CRPOipmVjilxEVrwl4,68PXzK2-NeGmyyjz8lyWXRqvWBkuUfl0QAWMyyyjKRw,AQACAAE/spider/197/) [^2] and to create my censored index [Clean-Spider](http://localhost:8888/USK@5ijbfKSJ4kPZTRDzq363CHteEUiSZjrO-E36vbHvnIU,ZEZqPXeuYiyokY2r0wkhJr5cy7KBH9omkuWDqSC6PLs,AQACAAE/clean-spider/104/) [^2].

## Requirements

- [OpenJDK](https://openjdk.org/) 11 or newer.
- [Freenet](https://freenetproject.org/)

## Build

1. Download the source code and extract it.
2. Open a command prompt in the root-directory of the extracted source code.
3. Run the following command: `gradlew distZip`. This will create the zip-archive `build/distributions/Spider.zip`.
4. Extract the generated zip-archive.

## Run

Run Spider with `bin/spider help` to view short usage information.

## Tasks

For each new edition of the freesite I run the following tasks in Spider:

- reset-all-highlight
- update-online 60
- crawl
- update-offline
- crawl
- reset-all-offline
- crawl
- add-freesite-from-fms
- add-freesite-from-frost
- crawl
- update-online
- crawl
- export-database

Notes:

- The task "crawl" was named "spider" in version 1.0.
- The tasks "add-freesite-from-frost", "reset-all-highlight", "reset-highlight" and "export-database" are not available in version 1.0.

## Contact

Author: Spider-Admin

Freemail: spider-admin@tlc66lu4eyhku24wwym6lfczzixnkuofsd4wrlgopp6smrbojf3a.freemail [^1]

Frost: Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

FMS: Spider-Admin

Sone: [Spider-Admin](http://localhost:8888/Sone/viewSone.html?sone=msXvLpwmDqprlrYZ5ZRZyi7VUcWQ~Wisznv9JkQuSXY) [^2]

I do not regularly read the email associated with GitHub.

## License

Spider by Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

[^1]: Freemail requires a running Freenet node
[^2]: Link requires a running Freenet node at http://localhost:8888/
