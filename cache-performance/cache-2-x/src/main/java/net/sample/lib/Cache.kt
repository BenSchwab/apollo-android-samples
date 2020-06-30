package net.sample.lib

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import kotlin.system.measureTimeMillis

object Cache {
    fun plentyOfWrites(context: Context, iterations: Long) {
        val helper = ApolloSqlHelper(context, "cache-1-4")
        //val cache = SqlNormalizedCacheFactory(helper)
        val cache = FixedSqlNormalizedCacheFactory(context)

        val client = ApolloClient.builder()
            .normalizedCache(cache)
            .dispatcher { it.run() }
            .serverUrl("https://net.sample/graphql")
            .build()

        val query = RepositoriesQuery()

        println("CachePerf: writing $iterations times in the cache using SQLDelight")
        1.until(iterations).forEach {
            println(it)
            println(measureTimeMillis { client.apolloStore.write(query, generateData()).execute() })
        }
    }


    private fun generateData() : RepositoriesQuery.Data {
        return RepositoriesQuery.Data(
                viewer = RepositoriesQuery.Viewer(
                        login = "toto",
                        repositories = RepositoriesQuery.Repositories(
                                nodes = getListOfRandomNodes()
                        )
                )
        )
    }

    private fun getListOfRandomNodes(): List<RepositoriesQuery.Node> {
        return 1.until(1000).map {
            RepositoriesQuery.Node(
                name = "This is a name + ${Math.random()}",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        }
    }
}