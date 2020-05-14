package de.stroeer.paperboy_light.services;

import de.stroeer.api.v1.article.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

// use DynamoMapper as soon as it's part of the v2 sdk, see
// https://github.com/aws/aws-sdk-java-v2/issues/35
public class ArticleMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMapper.class);

    public static List<Article> fromList(List<Map<String, AttributeValue>> items) {
        return items.stream()
                .map(ArticleMapper::fromMap)
                .filter(it -> !it.getType().equals("undefined"))
                .collect(Collectors.toList());
    }

    public static Article fromMap(Map<String, AttributeValue> attributes) {
        final String id = attributes.get("id").s();
        LOG.debug("transforming {}", id);

        Article.Builder article = Article.newBuilder();

        if (attributes.containsKey("authors")) {
            authors(attributes.get("authors").l()).forEach(article::addAuthors);
        }

        if (attributes.containsKey("canonical_url")) {
            article.setCanonicalUrl(attributes.get("canonical_url").s());
        }

        if (attributes.containsKey("elements")) {
            elements(attributes.get("elements").l()).forEach(article::addElements);
        }
//        fields(attributes.get("fields").m()).forEach((x,y) -> {
//            article.setField(Descriptors.FieldDescriptor()., y);
//        });
        article.setHeadline(attributes.get("headline").s());

//        if (attributes.containsKey("keywords")) {
//            article.setKeywords(keywords(attributes.get("keywords").l()));
//        }

        if (attributes.containsKey("metadata")) {
            article.setMetadata(metadata(attributes.get("metadata").m()));
        }

        article.setSectionPath(attributes.get("section_path").s());
        var ct = switch (attributes.get("type").s().toLowerCase()) {
            case "article" -> ContentType.CONTENT_TYPE_ARTICLE;
            case "image" -> ContentType.CONTENT_TYPE_IMAGE;
            case "video" -> ContentType.CONTENT_TYPE_VIDEO;
            case "gallery" -> ContentType.CONTENT_TYPE_GALLERY;
            case "oembed" -> ContentType.CONTENT_TYPE_OEMBED;
            default -> ContentType.CONTENT_TYPE_UNSPECIFIED;
        };
        article.setType(ct);

        article.setWebUrl(attributes.get("web_url").s());
        return article.build();
    }

    private static List<Author> authors(List<AttributeValue> attributes) {
        final List<Author> authors = new ArrayList<>();
        for (AttributeValue am : attributes) {
            final Map<String, AttributeValue> aa = am.m();
            Author.Builder author = Author.newBuilder();

//      if (aa.containsKey("id")) {
//        builder.setId(aa.get("id").s());
//      }
//      if (aa.containsKey("alias")) {
//        author.setAlias(aa.get("alias").s());
//      }
            if (aa.containsKey("name")) {
                author.setFirstName(aa.get("name").s());
            }
            authors.add(author.build());
        }
        return authors;
    }

    private static Map<String, String> fields(Map<String, AttributeValue> attributes) {
        final Map<String, String> fields = new HashMap<>();
        for (Entry<String, AttributeValue> field : attributes.entrySet()) {
            fields.put(field.getKey(), field.getValue().s());
        }
        return fields;
    }

    private static Metadata metadata(Map<String, AttributeValue> attributes) {
        final Instant validFromDate = Instant.parse(attributes.get("valid_from_date").s());
        final Instant validToDate = Instant.parse(attributes.get("valid_to_date").s());

        Metadata.Builder metadata = Metadata.newBuilder();
//        metadata.setValidFromDate();
//        metadata.setValidToDate();
        metadata.setState(attributes.get("state").s());

//        final Instant transformationDate = Instant.parse(attributes.get("transformation_date").s());
//        metadata.setTransformationDate(transformationDate);
        return metadata.build();
    }

    private static String[] keywords(List<AttributeValue> attributes) {
        final List<String> keywords = new ArrayList<>();
        for (AttributeValue keyword : attributes) {
            keywords.add(keyword.s());
        }
        return keywords.toArray(new String[0]);
    }

    private static List<Element> elements(List<AttributeValue> attributes) {
        final List<Element> elements = new ArrayList<>();

//        for (AttributeValue attribute : attributes) {
//            final Map<String, AttributeValue> ea = attribute.m();
//            // this is set, or maybe not - who knows
//            final AttributeValue avid = ea.get("guid");
//            final String guid = avid != null ? avid.s() : "";
//            final Element element =
//                    new Element(guid, CType.byName(ea.get("type").s()), new ArrayList<>());
//
//            // add relations
//            final List<AttributeValue> ra = ea.get("relations").l();
//            for (AttributeValue value : ra) {
//                element.getRelations().add(ElementRelationType.byType(value.s()));
//            }
//            elements.add(element);
//
//            final List<Asset> assets = assets(ea);
//            element.setAssets(assets);
//        }
        return elements;
    }

    private static List<Asset> assets(Map<String, AttributeValue> attributes) {
        final List<AttributeValue> al = attributes.get("assets").l();
        final List<Asset> assets = new ArrayList<>();
//        for (AttributeValue value : al) {
//            final Map<String, AttributeValue> am = value.m();
//            final AssetType type = AssetType.byType(am.get("type").s());
//
//            // use reflection magic here?
//            Asset asset = null;
//            if (type == AssetType.IMAGE) {
//                asset = new ImageAsset();
//            } else if (type == AssetType.VIDEO) {
//                asset = new VideoAsset();
//            } else if (type == AssetType.URL) {
//                asset = new UrlAsset();
//            } else if (type == AssetType.METADATA) {
//                asset = new MetadataAsset();
//            }
//
//            if (asset != null) {
//                final Map<String, AttributeValue> mf = am.get("fields").m();
//                for (Entry<String, AttributeValue> entry : mf.entrySet()) {
//                    asset.getFields().put(entry.getKey(), entry.getValue().s());
//                }
//                asset.setIndex(Optional.of(Long.parseLong(am.get("index").n())));
//                if (am.containsKey("mime_type")) {
//                    asset.setMimeType(Optional.of(am.get("mime_type").s()));
//                }
//                assets.add(asset);
//            }
//        }
        return assets;
    }
}
