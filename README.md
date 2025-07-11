# Spider

Spider crawls freesites in [Hyphanet](https://www.hyphanet.org/), extracts various information and creates multiple indexes of these freesites. It is currently used by me to create my uncensored index [Spider](http://localhost:8888/USK@nLTpFO0hKAp9AaaChDzk~hA95CRPOipmVjilxEVrwl4,68PXzK2-NeGmyyjz8lyWXRqvWBkuUfl0QAWMyyyjKRw,AQACAAE/spider/486/) [^1], my censored index [Clean-Spider](http://localhost:8888/USK@5ijbfKSJ4kPZTRDzq363CHteEUiSZjrO-E36vbHvnIU,ZEZqPXeuYiyokY2r0wkhJr5cy7KBH9omkuWDqSC6PLs,AQACAAE/clean-spider/394/) [^1] and my lightly censored index [Gentle Weaver](http://localhost:8888/USK@vl06Fb1XuqxOPAremAXxe2P89D7~sAQvIt1X-r2HzDw,GFURsB422HTZ4k9OM8M9CHLnaSkdUjlEZmsse9XX0-M,AQACAAE/sfw-spider/4/) [^1].

## Requirements

- [OpenJDK](https://openjdk.org/) 17 or newer.
- [Hyphanet](https://www.hyphanet.org/)

## Build

1. Download the source code and extract it.
2. Open a command prompt in the root-directory of the extracted source code.
3. Run the following command: `gradlew distZip`. This will create the zip-archive `build/distributions/Spider.zip`.
4. Extract the generated zip-archive.

## Run

Run Spider with `bin/spider help` to view short usage information. Before the first start, the database of Spider must be initialized with `bin/spider init`. Alternatively you can use my database of Spider, all existing database dumps can be found on [Spider - Database](http://localhost:8888/USK@-nBg4wrsA8fSoTGCqFb0kZjAetx4V61VemQlfvJ4GB0,K~v87jvyRl85a4U6mfI8L6ByNIp4Vn~0PwSzuRNgzus,AQACAAE/spider-database/17/) [^1].

## Important files

### Activelink

All [activelink](https://github.com/hyphanet/wiki/wiki/Activelink) images can be found in the folder `activelink`.

- activelink.xcf = Activelink for Spider
- activelink-clean.xcf = Activelink for Clean-Spider
- activelink-sfw.xcf = Activelink for Gentle Weaver
- activelink-source.xcf = Activelink for the source code of Spider
- activelink-database.xcf = Activelink for the database of Spider

The xcf files can be opened with [GIMP](https://www.gimp.org/).

### Settings

All settings files can be found in the folder `src/main/dist`.

- spider.properties = Settings file for Spider
- spider-clean.properties = Settings file for Clean-Spider
- spider-sfw.properties = Settings file for Gentle Weaver

Spider always uses the settings file `spider.properties`, so you have to rename the files for the usage in Clean-Spider and Gentle Weaver.

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


## Publish the freesite

I used [jSite](http://localhost:8888/USK@1waTsw46L9-JEQ8yX1khjkfHcn--g0MlMsTlYHax9zQ,oYyxr5jyFnaTsVGDQWk9e3ddOWGKnqEASxAk08MHT2Y,AQACAAE/jSite/19/) [^1] to publish my indexes. Just create a new project for each index in jSite and publish it with `java -cp path/to/jSite/jSite-0.14-jar-with-dependencies.jar de.todesbaum.jsite.main.CLI --project=projectname`.

Additionally I shared the private key of Clean-Spider with ArneBab, Bombe, xor and nextgens. I splitted the private key with [ssss](http://point-at-infinity.org/ssss/) using `ssss-split -t 2 -n 4` and send one part to each of them. You need at least 2 parts to recover the private key.

## Contact

Author: Spider-Admin

Freemail: spider-admin@tlc66lu4eyhku24wwym6lfczzixnkuofsd4wrlgopp6smrbojf3a.freemail [^2]

Frost: Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

FMS: Spider-Admin

Sone: [Spider-Admin](http://localhost:8888/Sone/viewSone.html?sone=msXvLpwmDqprlrYZ5ZRZyi7VUcWQ~Wisznv9JkQuSXY) [^1]

I do not regularly read the email associated with GitHub.

## License

Spider by Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

[^1]: Link requires a running Hyphanet node at http://localhost:8888/
[^2]: Freemail requires a running Hyphanet node
