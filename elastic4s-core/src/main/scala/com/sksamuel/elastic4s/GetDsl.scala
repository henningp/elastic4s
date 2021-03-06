package com.sksamuel.elastic4s

import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.{Client, Requests}
import org.elasticsearch.index.VersionType
import org.elasticsearch.index.get.GetField
import org.elasticsearch.search.fetch.source.FetchSourceContext

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait GetDsl extends IndexesTypesDsl {

  class GetWithIdExpectsFrom(id: String) {
    def from(index: IndexesTypes): GetDefinition = new GetDefinition(index, id)
    def from(index: IndexType): GetDefinition = new GetDefinition(index.index, id)
    def from(index: String, `type`: String): GetDefinition = from(IndexesTypes(index, `type`))
    def from(index: String): GetDefinition = new GetDefinition(index, id)
  }

  implicit object GetDefinitionExecutable extends Executable[GetDefinition, GetResponse, RichGetResponse] {
    override def apply(c: Client, t: GetDefinition): Future[RichGetResponse] = {
      injectFutureAndMap(c.get(t.build, _))(RichGetResponse)
    }
  }
}

case class GetDefinition(indexesTypes: IndexesTypes, id: String) {

  private val _builder = Requests.getRequest(indexesTypes.index).`type`(indexesTypes.typ.orNull).id(id)
  def build = _builder

  def fetchSourceContext(context: Boolean) = {
    _builder.fetchSourceContext(new FetchSourceContext(context))
    this
  }

  def fetchSourceContext(context: FetchSourceContext) = {
    _builder.fetchSourceContext(context)
    this
  }

  def fields(fs: String*): GetDefinition = fields(fs)
  def fields(fs: Iterable[String]): GetDefinition = {
    _builder.fields(fs.toSeq: _*)
    this
  }

  def ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields: Boolean) = {
    _builder.ignoreErrorsOnGeneratedFields(ignoreErrorsOnGeneratedFields)
    this
  }

  def parent(p: String) = {
    _builder.parent(p)
    this
  }

  def preference(pref: Preference): GetDefinition = preference(pref.elastic)
  def preference(pref: String): GetDefinition = {
    _builder.preference(pref)
    this
  }

  def realtime(r: Boolean) = {
    _builder.realtime(r)
    this
  }

  def refresh(refresh: Boolean) = {
    _builder.refresh(refresh)
    this
  }

  def routing(r: String) = {
    _builder.routing(r)
    this
  }

  def version(version: Long) = {
    _builder.version(version)
    this
  }

  def versionType(versionType: VersionType) = {
    _builder.versionType(versionType)
    this
  }
}

case class RichGetResponse(original: GetResponse) extends AnyVal {

  import scala.collection.JavaConverters._

  @deprecated("use field(name)", "2.0.0")
  def getField(name: String): GetField = field(name)
  def field(name: String): GetField = original.getField(name)
  def fieldOpt(name: String): Option[GetField] = Option(field(name))

  @deprecated("use fields", "2.0.0")
  def getFields = original.getFields
  def fields: Map[String, GetField] = original.getFields.asScala.toMap

  @deprecated("use id", "2.0.0")
  def getId: String = id
  def id: String = original.getId

  @deprecated("use index", "2.0.0")
  def getIndex : String = index
  def index: String = original.getIndex

  def source = original.getSource
  def sourceAsBytes = original.getSourceAsBytes
  def sourceAsString: String = original.getSourceAsString

  @deprecated("use `type`", "2.0.0")
  def getType : String = `type`
  def `type`: String = original.getType

  @deprecated("use version", "2.0.0")
  def getVersion : Long = version
  def version: Long = original.getVersion

  def isExists: Boolean = original.isExists
  def isSourceEmpty: Boolean = original.isSourceEmpty
  def iterator: Iterator[GetField] = original.iterator.asScala
}

case class RichGetField(original: GetField) extends AnyVal {

  import scala.collection.JavaConverters._

  def name: String = original.getName
  def value: AnyRef = original.getValue
  def values: Seq[AnyRef] = original.getValues.asScala
  def isMetadataField: Boolean = original.isMetadataField
  def iterator: Iterator[AnyRef] = original.iterator.asScala
}