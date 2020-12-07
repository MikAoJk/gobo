package no.skatteetaten.aurora.gobo.graphql.gobo

import com.expediagroup.graphql.spring.operations.Query
import graphql.schema.DataFetchingEnvironment
import no.skatteetaten.aurora.gobo.domain.FieldService
import no.skatteetaten.aurora.gobo.graphql.GoboInstrumentation
import no.skatteetaten.aurora.gobo.security.checkValidUserToken
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class GoboQuery(private val fieldService: FieldService) : Query {

    private val startTime = Instant.now()

    fun gobo(fieldName: String?, dfe: DataFetchingEnvironment): Gobo {
        dfe.checkValidUserToken()

        fieldName?.let {
            fieldService.getFieldWithName(it)

        } ?: fieldService.getAllFields()


        val fields = goboInstrumentation.fieldUsage.fields.map {
            val userFieldClientList = goboInstrumentation.fieldUsage.getFieldUsers(it.key)
            GoboFieldUsage(it.key, it.value.sum(), userFieldClientList)
        }
        val users = goboInstrumentation.userUsage.users.map { GoboUser(it.key, it.value.sum()) }
        return Gobo(startTime, GoboUsage(fields, users))
    }
}
