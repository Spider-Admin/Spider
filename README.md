# Spider

Spider crawls freesites in [Freenet](https://freenetproject.org/), extracts various information and creates an index of these freesites. It is currently used by me to create my uncensored index [Spider](http://localhost:8888/USK@nLTpFO0hKAp9AaaChDzk~hA95CRPOipmVjilxEVrwl4,68PXzK2-NeGmyyjz8lyWXRqvWBkuUfl0QAWMyyyjKRw,AQACAAE/spider/286/) [^1] and to create my censored index [Clean-Spider](http://localhost:8888/USK@5ijbfKSJ4kPZTRDzq363CHteEUiSZjrO-E36vbHvnIU,ZEZqPXeuYiyokY2r0wkhJr5cy7KBH9omkuWDqSC6PLs,AQACAAE/clean-spider/192/) [^1].

## Requirements

- [OpenJDK](https://openjdk.org/) 17 or newer.
- [Freenet](https://freenetproject.org/)

## Build

1. Download the source code and extract it.
2. Open a command prompt in the root-directory of the extracted source code.
3. Run the following command: `gradlew distZip`. This will create the zip-archive `build/distributions/Spider.zip`.
4. Extract the generated zip-archive.

## Run

Run Spider with `bin/spider help` to view short usage information. Before the first start, the database of Spider must be initialized with `bin/spider init`.

## Tasks

For each new edition of the freesite I run the following tasks in Spider:

- reset-all-highlight
- update-online 60
- crawl
- update-offline
- crawl
- ~~reset-all-offline~~
- ~~crawl~~
- add-freesite-from-fms
- add-freesite-from-frost
- crawl
- update-online
- crawl
- export-database

You can either run these tasks individually or you can run them as task list.

### Task list

Run the above tasks as task list using `bin/spider run-task-list`. Spider will execute each task in the given order one by another. Additionally Spider will save the state such that you can interrupt the process at any time and can continue where you previously interrupted it. You can restart the task list with `bin/spider reset-task-list` (once the task list is finished it will automatically reset itself) and show the current progress with `bin/spider show-task-list`.

## Set up a development environment

1. Run `git clone https://github.com/Spider-Admin/Spider.git`.
2. Change to the cloned repository.
3. Run `gradlew clean cleanEclipse eclipse`.
4. Copy all files from `src/main/dist` to the current directory.
5. Import the project from the current directory into [Eclipse](https://eclipseide.org/).

## Contact

Author: Spider-Admin

Freemail: spider-admin@tlc66lu4eyhku24wwym6lfczzixnkuofsd4wrlgopp6smrbojf3a.freemail [^2]

Frost: Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

FMS: Spider-Admin

Sone: [Spider-Admin](http://localhost:8888/Sone/viewSone.html?sone=msXvLpwmDqprlrYZ5ZRZyi7VUcWQ~Wisznv9JkQuSXY) [^1]

I do not regularly read the email associated with GitHub.

## License

Spider by Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

[^1]: Link requires a running Freenet node at http://localhost:8888/
[^2]: Freemail requires a running Freenet node
